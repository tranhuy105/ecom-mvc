package com.tranhuy105.common.entity;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "settings")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Setting {
    @Id
    @Column(nullable = false, length = 128, name = "`key`")
    private String key;
    @Column(nullable = false, length = 1024)
    private String value;

    @Enumerated(EnumType.STRING)
    @Column(length = 45, nullable = false)
    private SettingCategory category;
}
