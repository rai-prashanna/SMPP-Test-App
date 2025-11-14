package com.prai.smpp_api.controller;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import com.prai.smpp_api.Greeting;
import com.prai.smpp_api.SubmitSM;
import io.github.cdimascio.dotenv.Dotenv;
import org.jsmpp.InvalidResponseException;
import org.jsmpp.PDUException;
import org.jsmpp.bean.*;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.extra.ProcessRequestException;
import org.jsmpp.extra.ResponseTimeoutException;
import org.jsmpp.session.*;
import org.jsmpp.session.connection.socket.NoTrustSSLSocketConnectionFactory;
import org.jsmpp.util.DeliveryReceiptState;
import org.jsmpp.util.InvalidDeliveryReceiptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@CrossOrigin(origins = "*")
public class SubmitSmController {
    private static final Logger log = LoggerFactory.getLogger(SubmitSmController.class);

    private static final String template = "Heclearllo, %s!";
    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/greeting")
    public Greeting greeting(@RequestParam(defaultValue = "World") String name) {
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }


    @PostMapping("/submit-msg")
    @ResponseStatus(code = HttpStatus.OK)
    String submitSmMsg(@RequestBody SubmitSM submitSM) {
        SMPPSession session = new SMPPSession(new NoTrustSSLSocketConnectionFactory());
        session.setQueueCapacity(10);
        Dotenv dotenv = Dotenv.load();
        String jsonString ="";
        String host = dotenv.get("SMPP_HOST");
        int port = Integer.parseInt(dotenv.get("SMPP_PORT"));
        try {
            log.info("Connecting");
            String systemId = session.connectAndBind(host, port,
                    new BindParameter(BindType.BIND_TRX,
                            dotenv.get("SYSTEM_ID"), dotenv.get("PASSWORD"),
                            "", TypeOfNumber.UNKNOWN,
                            NumberingPlanIndicator.UNKNOWN, null,
                            InterfaceVersion.IF_34));
            log.info("Connected with SMSC with system id {}", systemId);

            // 🔹 Set up a listener for delivery receipts and MO messages
            session.setMessageReceiverListener(new MessageReceiverListener() {
                @Override
                public void onAcceptDeliverSm(DeliverSm deliverSm) throws ProcessRequestException {
                    try {
                        if (MessageType.SMSC_DEL_RECEIPT.containedIn(deliverSm.getEsmClass())) {
                            // ✅ This is a Delivery Receipt
                            DeliveryReceipt delRec = deliverSm.getShortMessageAsDeliveryReceipt();

                            String messageId = delRec.getId();
                            DeliveryReceiptState state = delRec.getFinalStatus();
//                            String doneDate = delRec.getDoneDate();
                            System.out.println("📩 Got delivery receipt for message_id=" + messageId
                                    + " with status=" + state );
                        } else {
                            // This is a Mobile-Originated message (regular deliver_sm)
                            String msg = new String(deliverSm.getShortMessage());
                            System.out.println("📨 Received message: " + msg);
                        }
                    } catch (InvalidDeliveryReceiptException e) {
                        log.error("Invalid delivery receipt encountered", e);
                    }
                }

                @Override
                public void onAcceptAlertNotification(AlertNotification alertNotification) {
                    System.out.println("Got alert notification");
                }

                @Override
                public DataSmResult onAcceptDataSm(DataSm dataSm, Session source)
                        throws ProcessRequestException {
                    System.out.println("Got data_sm");
                    return null;
                }
            });

            // 🔹 Submit an SMS with delivery receipt requested
            SubmitSmResult submitSmResult = session.submitShortMessage("CMT",
                    TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.ISDN, "15551234567",
                    TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.ISDN, "447700900007",
                    new ESMClass(0), (byte) 0, (byte) 0,
                    null, null,
                    new RegisteredDelivery((3)),
                    (byte) 0,
                    new GeneralDataCoding(),
                    (byte) 0,
                    "Queue full test".getBytes());

            ObjectMapper objectMapper = new ObjectMapper();
            jsonString = objectMapper.writeValueAsString(submitSmResult);
            // Keep session open to receive DRs
/*
            Thread.sleep(60000);
*/

        } catch (ResponseTimeoutException | InvalidResponseException | NegativeResponseException | IOException |
                 PDUException e) {
            throw new RuntimeException(e);
        } finally {
            session.unbindAndClose();
        }
        return jsonString;
    }
}

