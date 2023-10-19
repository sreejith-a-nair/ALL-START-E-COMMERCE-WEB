package com.mydemo.demoproject.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class  CategoryInfo {
   /*datatype change*/
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID uuid;

    @Column(name = "Category_Name",unique = true)
    private  String categoryname;

    @Column(name = "Category_Description")
    private String description;

    @OneToMany(mappedBy = "category")
    private List<ProductInfo> products;

    @OneToMany(mappedBy = "productInfo")
    private List<Offer> offer;


    private boolean enable;


}
