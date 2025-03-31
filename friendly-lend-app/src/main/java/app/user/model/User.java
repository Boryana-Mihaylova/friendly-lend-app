package app.user.model;


import app.item.model.Item;

import app.purchase.model.ItemPurchase;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {

        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private UUID id;


        @Column(unique = true, nullable = false)
        private String username;

        private String firstName;

        private String lastName;

        @Column(nullable = false)
        private String password;

        @Column(unique = true)
        private String email;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private UserRole role;

        private boolean isActive;

        @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
        private List<Item> items = new ArrayList<>();

        @OneToMany(fetch = FetchType.EAGER, mappedBy = "owner")
        private List<ItemPurchase> purchases = new ArrayList<>();


}
