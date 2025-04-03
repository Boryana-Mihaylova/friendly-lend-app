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


    @ManyToOne(optional = false)
    private User owner;




}
