package com.prai.smpp_api.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.*;
import com.prai.smpp_api.entity.DeliveryReceiptEntity;
import org.jsmpp.bean.DeliveryReceipt;
import org.jsmpp.util.DeliveryReceiptState;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.boot.jackson.JsonObjectDeserializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@JsonComponent
public class DeliveryReceiptJsonComponent {
    public static class Serializer extends JsonSerializer<DeliveryReceipt> {

        @Override
        public void serialize(DeliveryReceipt deliveryReceipt, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField(
                    "id",deliveryReceipt.getId());
            jsonGenerator.writeStringField(
                    "sub",intToString(deliveryReceipt.getSubmitted(), 3));
            jsonGenerator.writeStringField(
                    "dlvrd",intToString(deliveryReceipt.getDelivered(), 3));
            jsonGenerator.writeStringField(
                    "submit_date",new SimpleDateFormat("yyMMddHHmm").format(deliveryReceipt.getSubmitDate()));
            jsonGenerator.writeStringField(
                    "done_date",new SimpleDateFormat("yyMMddHHmm").format(deliveryReceipt.getDoneDate()));
            jsonGenerator.writeNumberField("stat",deliveryReceipt.getFinalStatus().value());
            jsonGenerator.writeStringField(
                    "err",deliveryReceipt.getError());
            jsonGenerator.writeStringField(
                    "text",deliveryReceipt.getText());
            jsonGenerator.writeEndObject();
        }
    }

    public static class Deserializer extends JsonDeserializer<DeliveryReceiptEntity> {

        @Override
        public DeliveryReceiptEntity deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
            try {
                ObjectCodec codec = jsonParser.getCodec();
                JsonNode tree = codec.readTree(jsonParser);

                String id = tree.get("id").textValue();
                int submitted = tree.get("sub").intValue();
                int delivered = tree.get("dlvrd").intValue();

                Date submitDate= new SimpleDateFormat("yyMMddHHmm").parse(tree.get("submit_date").textValue());
                Date doneDate=new SimpleDateFormat("yyMMddHHmm").parse(tree.get("done_date").textValue());

                String error = tree.get("err").textValue();
                String text = tree.get("text").textValue();
                var finalStatus= tree.get("stat").intValue();

                return new DeliveryReceiptEntity(id,submitted,delivered,submitDate,doneDate,finalStatus,error,text);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

        }

    }


    private static String intToString(int value, int digit) {
        StringBuilder stringBuilder = new StringBuilder(digit);
        stringBuilder.append(value);

        while(stringBuilder.length() < digit) {
            stringBuilder.insert(0, "0");
        }

        return stringBuilder.toString();
    }
}
