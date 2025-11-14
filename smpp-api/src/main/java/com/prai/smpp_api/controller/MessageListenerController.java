package com.prai.smpp_api.controller;

import com.prai.smpp_api.InputMessage;
import com.prai.smpp_api.OutputMessage;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.time.Clock;
import java.time.Instant;
import java.util.Map;

@Controller
@Slf4j
public class MessageListenerController {
//    SMPPSession session = new SMPPSession(new NoTrustSSLSocketConnectionFactory());
    private static final Logger log = LoggerFactory.getLogger(MessageListenerController.class);

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public Map<String, String> message(InputMessage message) {
        log.info("Input Message "+message);
        return Map.of("key", "value");
    }

//    public void messageListener() {
//        try {
//            session.setQueueCapacity(10);
//            Dotenv dotenv = Dotenv.load();
//            String host = dotenv.get("SMPP_HOST");
//            int port = Integer.parseInt(dotenv.get("SMPP_PORT"));
//            log.info("Connecting");
//            String systemId = session.connectAndBind(host, port,
//                    new BindParameter(BindType.BIND_TRX,
//                            dotenv.get("SYSTEM_ID"), dotenv.get("PASSWORD"),
//                            "", TypeOfNumber.UNKNOWN,
//                            NumberingPlanIndicator.UNKNOWN, null,
//                            InterfaceVersion.IF_34));
//            log.info("Connected with SMSC with system id {}", systemId);
//            session.setMessageReceiverListener(new MessageReceiverListener() {
//                @Override
//                public DataSmResult onAcceptDataSm(DataSm dataSm, Session session) throws ProcessRequestException {
//                    log.info("onAcceptDataSm");
//                    return null;
//                }
//
//                @Override
//                public void onAcceptDeliverSm(DeliverSm deliverSm) throws ProcessRequestException {
//                    try {
//                        if (MessageType.SMSC_DEL_RECEIPT.containedIn(deliverSm.getEsmClass())) {
//                            DeliveryReceipt delRec = deliverSm.getShortMessageAsDeliveryReceipt();
//
//                            String messageId = delRec.getId();
//                            DeliveryReceiptState state = delRec.getFinalStatus();
//                            log.info("📩 Got delivery receipt for message_id={} with status={}", messageId, state);
////                            System.out.println("📩 Got delivery receipt for message_id=" + messageId
////                                    + " with status=" + state );
//                        } else {
//                            // This is a Mobile-Originated message (regular deliver_sm)
//                            String msg = new String(deliverSm.getShortMessage());
//                            log.info("📨 Received message: {}", msg);
////                            System.out.println("📨 Received message: " + msg);
//                        }
//                    } catch (InvalidDeliveryReceiptException e) {
//                        log.error("Invalid delivery receipt encountered", e);
//                    }
//                }
//
//                @Override
//                public void onAcceptAlertNotification(AlertNotification alertNotification) {
//                    log.info("Got alert notification");
////                    System.out.println("Got alert notification");
//                }
//            });
//
//        }
//        catch (Exception e) {
//            session.unbindAndClose();
//            throw new RuntimeException(e);
//        }
//    }


}


