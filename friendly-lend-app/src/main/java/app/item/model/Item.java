package app.item.model;


import app.favorite.model.Favorite;
import app.purchase.model.ItemPurchase;
import app.user.model.User;
import jakarta.persistence.*;
import lombok.*;


import java.math.BigDecimal;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SizeItem size;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Period period;




    @ManyToOne(optional = false)
    private User owner;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "item")
    private List<ItemPurchase> purchases = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "item")
    private List<Favorite> favorites = new ArrayList<>();


}
