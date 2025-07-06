package app.purchase.model;

import app.item.model.Gender;
import app.item.model.Item;
import app.item.model.Period;
import app.item.model.SizeItem;
import app.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ItemPurchase {

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
    private Gender gender;

    @Column(nullable = false)
    private SizeItem size;

    @Column(nullable = false)
    private BigDecimal price = BigDecimal.ONE;

    @Column(nullable = false)
    private Period period;

    @ManyToOne(optional = false)
    private User owner;

    @ManyToOne
    @JoinColumn(nullable = false, columnDefinition = "CHAR(36)")
    private Item item;

}
