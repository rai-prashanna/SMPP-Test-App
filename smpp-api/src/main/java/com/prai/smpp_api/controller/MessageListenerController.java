package com.prai.smpp_api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.prai.smpp_api.SubmitSM;
import com.prai.smpp_api.entity.DeliveryReceiptEntity;
import com.prai.smpp_api.repository.DeliveryReceiptRepository;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.jsmpp.bean.*;
import org.jsmpp.extra.ProcessRequestException;
import org.jsmpp.session.*;
import org.jsmpp.session.connection.socket.NoTrustSSLSocketConnectionFactory;
import org.jsmpp.util.DeliveryReceiptState;
import org.jsmpp.util.InvalidDeliveryReceiptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

import static com.prai.smpp_api.service.DeliveryReceiptService.transformToEntity;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;

@RestController
@Slf4j
public class MessageListenerController {

    @Autowired
    private ObjectMapper objectMapper;

    SMPPSession session = new SMPPSession(new NoTrustSSLSocketConnectionFactory());
    private static final Logger log = LoggerFactory.getLogger(MessageListenerController.class);

    private final String QUEUE_NAME = "queue";

    @Autowired
    private DeliveryReceiptRepository deliveryReceiptRepository;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public MessageListenerController() {
        messageListener();
    }

    @JmsListener(destination = QUEUE_NAME)
    public void receiveMessage(String message) throws JsonProcessingException {
        DeliveryReceiptEntity entity=objectMapper.readValue(message,DeliveryReceiptEntity.class);
        deliveryReceiptRepository.save(entity);
        log.info("📩 Saved delivery receipt with message= {} to db ", message);
        log.info("📩 Forwarding message: {} to topic {}", message, "/topic/messages");
        List<DeliveryReceiptEntity> drs=deliveryReceiptRepository.findAll();
//        drs.stream().map(DeliveryReceiptEntity::getMe)


        var dr_json_list=objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(drs);
        // Forward to WebSocket subscribers
        messagingTemplate.convertAndSend("/topic/messages",dr_json_list);
    }

    @GetMapping("/drs")
    @ResponseStatus(code = HttpStatus.OK)
    List<DeliveryReceiptEntity> submitSmMsg() {
        return deliveryReceiptRepository.findAll();
    }


    public void messageListener() {
        try {
            session.setQueueCapacity(10);
            Dotenv dotenv = Dotenv.load();
            String host = dotenv.get("SMPP_HOST");
            int port = Integer.parseInt(dotenv.get("SMPP_PORT"));
            log.info("Connecting");
            String systemId = session.connectAndBind(host, port,
                    new BindParameter(BindType.BIND_TRX,
                            dotenv.get("SYSTEM_ID"), dotenv.get("PASSWORD"),
                            "", TypeOfNumber.UNKNOWN,
                            NumberingPlanIndicator.UNKNOWN, null,
                            InterfaceVersion.IF_34));
            log.info("Connected with SMSC with system id {}", systemId);
            session.setMessageReceiverListener(new MessageReceiverListener() {
                @Override
                public DataSmResult onAcceptDataSm(DataSm dataSm, Session session) throws ProcessRequestException {
                    log.info("onAcceptDataSm");
                    return null;
                }

                @Override
                public void onAcceptDeliverSm(DeliverSm deliverSm) throws ProcessRequestException {
                    try {
                        if (MessageType.SMSC_DEL_RECEIPT.containedIn(deliverSm.getEsmClass())) {
                            DeliveryReceipt delRec = deliverSm.getShortMessageAsDeliveryReceipt();

                            String messageId = delRec.getId();
                            DeliveryReceiptState state = delRec.getFinalStatus();
                            log.info("📩 Got delivery receipt for message_id={} with status={}", messageId, state);
                            log.info("📩 Saving delivery receipt with message_id={} with status={} to queue={} ", messageId, state,QUEUE_NAME);

                            jmsTemplate.convertAndSend(QUEUE_NAME, objectMapper.writeValueAsString(delRec));

                            log.info("📩 Saved delivery receipt with message_id={} with status={} to queue={} ", messageId, state,QUEUE_NAME);

                        } else {
                            // This is a Mobile-Originated message (regular deliver_sm)
                            String msg = new String(deliverSm.getShortMessage());
                            log.info("📨 Received message: {}", msg);
//                            System.out.println("📨 Received message: " + msg);
                        }
                    } catch (InvalidDeliveryReceiptException e) {
                        log.error("Invalid delivery receipt encountered", e);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void onAcceptAlertNotification(AlertNotification alertNotification) {
                    log.info("Got alert notification");
//                    System.out.println("Got alert notification");
                }
            });

        }
        catch (Exception e) {
            session.unbindAndClose();
            throw new RuntimeException(e);
        }
    }




}


