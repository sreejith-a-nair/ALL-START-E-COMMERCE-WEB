package com.mydemo.demoproject.Entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Entity
    @Setter
    @Getter
   @Builder

    public class UserEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
        @GenericGenerator(name = "uuid2", strategy = "uuid2")
        @Type(type = "org.hibernate.type.UUIDCharType")
        private UUID uuid;

        @Column(name = "user_name")
        private String username;

        @Column(name = "first_name")
        private String firstname;

        @Column(name = "last_name")
        private String lastname;

        @Column(name = "contact")
        private Long contact;

        @Column(name = "email")
        private String email;

        @Column(name = "password")
        private String password;


        @Column(name = "roles")
        private String role;

        @Column(unique = true)
        private String phone;


        private boolean enable;


        private boolean verify=false;

/*old cart id*/
//        @OneToOne(cascade = CascadeType.ALL)
//        @JoinColumn(name = "cart_id")
//        private Cart cart;
//        end

    @OneToMany(mappedBy = "userEntity")
    @ToString.Exclude
    private List<Cart> cartItems = new ArrayList<>();


    @OneToMany(mappedBy = "userEntity")
    private List<Address>addresses=new ArrayList<>();

    @Override
    public String toString() {
        return "UserEntity{" +
                "uuid=" + uuid +
                ", username='" + username + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", contact=" + contact +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", phone='" + phone + '\'' +
                ", enable=" + enable +
                ", verify=" + verify +
                '}';
    }

    }