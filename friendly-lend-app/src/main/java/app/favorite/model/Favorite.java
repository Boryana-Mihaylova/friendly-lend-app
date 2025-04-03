package app.favorite.model;


import app.item.model.Item;
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
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String imageUrl;

    @ManyToOne(optional = false)
    private User owner;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Item item;

}
