package com.mydemo.demoproject.Entity;

import com.mydemo.demoproject.Entity.enumlist.CancelReason;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CancelOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID uuid;

    private LocalDate cancellationDate;

    @ManyToOne
    @JoinColumn(name =" user_id")
    private UserEntity userEntity;

    @ManyToOne
    @JoinColumn(name =" order_id")
    private Order order;

    @Enumerated(EnumType.STRING)
    CancelReason cancelReason;

}
