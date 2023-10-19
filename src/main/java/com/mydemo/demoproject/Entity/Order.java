package com.mydemo.demoproject.Entity;

import com.mydemo.demoproject.Entity.enumlist.OrderStatus;
import com.mydemo.demoproject.Entity.enumlist.PaymentMode;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL)
    @ToString.Exclude
    List<OrderItems> orderItems=new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "address_id")
    Address address;

    @Enumerated(EnumType.STRING)
    PaymentMode paymentMode;

    @Enumerated(EnumType.STRING)
    OrderStatus orderStatus;

    Double totalPrice;

    private LocalDateTime orderDate;


    private LocalDate deliveryDate;

    private boolean enable;

}
