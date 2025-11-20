package com.prai.smpp_api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.jsmpp.util.DeliveryReceiptState;

import java.util.Date;

@Entity
@Table(name = "delivery_receipt")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DeliveryReceiptEntity {
    @Id
    @EqualsAndHashCode.Include
    public String id;

    private int submitted;
    private int delivered;

    private Date submitDate;
    private Date doneDate;
    private int finalStatus;
    private String error;
    private String text;


}