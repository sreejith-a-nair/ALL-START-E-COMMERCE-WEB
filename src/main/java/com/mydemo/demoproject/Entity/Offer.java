package com.mydemo.demoproject.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID uuid;

    @Column(name = "Category_Off_Percentage")
    private int CategoryOffPercentage;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;

    private boolean enabled ;

    @ManyToOne
    @JoinColumn(name = "category_id")

    private CategoryInfo categoryInfo;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductInfo productInfo;



    private int count;

    @Override
    public String toString() {
        return "Offer{" +
                "uuid=" + uuid +
                ", CategoryOffPercentage=" + CategoryOffPercentage +
                ", expiryDate=" + expiryDate +
                ", enabled=" + enabled +
                ", count=" + count +
                '}';
    }
}
