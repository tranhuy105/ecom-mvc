package com.tranhuy105.common.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "brands")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 45, unique = true)
    private String name;

    @Column(nullable = false, length = 128)
    private String logo;


    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "brand_categories",
            joinColumns = @JoinColumn(name = "brand_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )

    private List<Category> categories = new ArrayList<>();

    public String getImagePath() {
        if (this.id == null || this.logo == null) {
            return "/images/default_user.jpg";
        }
//        return String.format("/category-images/%s/%s", this.id, this.logo);
        return "/images/default_user.jpg";
    }

    public Brand(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
