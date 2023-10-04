package com.mydemo.demoproject.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "product_info")
public class ProductInfo {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID uuid;

    @Column(name = "product_name",unique = true)
    private String name;

    @Column(name = "product_description")
    private String description;


    private boolean enable;

    @Column(name = "product_stock")
    private Long stock;

    @Column(name = "product_price")
     private float price;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryInfo category;

    @OneToMany(mappedBy = "product_id",cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Image> images;


    @Override
    public String toString() {
        return "Product{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price='" + price + '\'' +
                ", stock='" + stock + '\'' +
                ", enabled=" + enable +
                '}';
    }

}
