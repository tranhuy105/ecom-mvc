package com.tranhuy105.common.entity;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "currencies")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 64)
    private String name;

    @Column(nullable = false, length = 4)
    private String symbol;

    @Column(nullable = false, length = 4)
    private String code;
}
