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
            String msg= submitSM.getMessage();
            // 🔹 Submit an SMS with delivery receipt requested
             var submitSmResult = session.submitShortMessage("CMT",
                    TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.ISDN, "15551234567",
                    TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.ISDN, "447700900007",
                    new ESMClass(0), (byte) 0, (byte) 0,
                    null, null,
                    new RegisteredDelivery((3)),
                    (byte) 0,
                    new GeneralDataCoding(),
                    (byte) 0,
                    msg.getBytes());

            ObjectMapper objectMapper = new ObjectMapper();
            String submitSmResultJson=objectMapper.writeValueAsString(submitSmResult);
            log.info("SubmitSmResult = {}", submitSmResultJson);
            return submitSmResultJson;
        } catch (ResponseTimeoutException | InvalidResponseException | NegativeResponseException | IOException |
                 PDUException e) {
            throw new RuntimeException(e);
        } finally {
            session.unbindAndClose();
        }
    }
}



//export interface Message {
//    id: string;
//    submitted: number;
//    delivered: number;
//    submitDate: number;
//    doneDate: number;
//    finalStatus: string;
//    error: string;
//    text: string;
//}