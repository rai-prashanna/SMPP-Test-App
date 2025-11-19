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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DeliveryReceiptEntity {
    @Id
    @EqualsAndHashCode.Include
    public String id;

    private Integer submitted;
    private Integer delivered;
    private Date submitDate;
    private Date doneDate;
    private DeliveryReceiptState finalStatus;
    private String error;
    private String text;
}