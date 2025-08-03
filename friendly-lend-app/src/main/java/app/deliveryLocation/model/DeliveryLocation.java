package app.deliveryLocation.model;


import app.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "delivery_locations")
public class DeliveryLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Location location;


    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Town town;


    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;



}
