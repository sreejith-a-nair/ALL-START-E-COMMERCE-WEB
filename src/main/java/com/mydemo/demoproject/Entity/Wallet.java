package com.mydemo.demoproject.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID uuid;

    private  float earnedMoney=0f;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    private float totalMoney=0f;
    /*new updates*/
    @Override
    public int hashCode() {
        return Objects.hash(uuid, earnedMoney, totalMoney);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Wallet other = (Wallet) obj;
        return Objects.equals(uuid, other.uuid);
    }
}
