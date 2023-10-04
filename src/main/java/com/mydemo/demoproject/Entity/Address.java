package com.mydemo.demoproject.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "org.hibernate.type.UUIDCharType")
    private  UUID uuid;
    private  String houseNumberOrName;
    private  String area;
    private  String city;
    private  String district;
    private  String state;
    private  Long   pincode;
    private  String landmark;
    private  boolean enabled;

    @ManyToOne
    @JoinColumn (name = "user_id")
    private  UserEntity userEntity;
}
