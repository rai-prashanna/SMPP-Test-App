package com.prai.smpp_api.entity;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import org.jsmpp.bean.*;
import java.io.Serializable;


@Entity
@Table(name = "submit_sm")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SubmitSmEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

//    @Type(JsonType.class)
//    @Column(columnDefinition = "jsonb")
//    private String optionalParameters;

    @Column
    private String serviceType;
    @Column
    private TypeOfNumber sourceAddrTon;
    @Column
    private NumberingPlanIndicator sourceAddrNpi;
    @Column
    private  String sourceAddr;
    @Column
    private TypeOfNumber destAddrTon;
    @Column
    NumberingPlanIndicator destAddrNpi;

    @Column String destinationAddr;
//    @Column ESMClass esmClass;
    @Column byte protocolId;
    @Column byte priorityFlag;
    @Column String scheduleDeliveryTime;
    @Column String validityPeriod;
//    @Column RegisteredDelivery registeredDelivery;
    @Column byte replaceIfPresentFlag;
//    @Column DataCoding dataCoding;
    @Column byte smDefaultMsgId;
    @Column byte[] shortMessage;

}
