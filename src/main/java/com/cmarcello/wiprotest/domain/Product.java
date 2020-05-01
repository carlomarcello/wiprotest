package com.cmarcello.wiprotest.domain;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Product {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(length = 36, columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(length = 255, columnDefinition = "varchar", updatable = true, nullable = false)
    private String description;

    @Column(columnDefinition = "numeric", length = 10, precision = 2, nullable = false)
    private BigDecimal value;

    @Column(columnDefinition = "timestamp with time zone", updatable = false)
    private Timestamp creationDate;

    @Column(nullable = false)
    private Boolean active;
}
