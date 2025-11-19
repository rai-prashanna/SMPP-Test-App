package com.prai.smpp_api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.*;
import org.hibernate.annotations.Type;
import jakarta.persistence.Column;

import java.io.Serializable;

@Entity
@Table(name = "submit_sm_result")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SubmitSmResultEntity implements Serializable {
    @Id
    @EqualsAndHashCode.Include
    private String messageId;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private String optionalParameters;


}
