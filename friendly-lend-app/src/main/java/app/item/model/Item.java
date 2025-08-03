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

import java.util.Date;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.PrePersist;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "items")
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

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ItemStatus status;




    @ManyToOne(optional = false)
    private User owner;

    @ManyToOne
    @JoinColumn(name = "borrower_id")
    private User borrower;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "item")
    private List<ItemPurchase> purchases = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "item")
    private List<Favorite> favorites = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
        if (this.status == null) {
            this.status = ItemStatus.AVAILABLE;
        }
    }


}
