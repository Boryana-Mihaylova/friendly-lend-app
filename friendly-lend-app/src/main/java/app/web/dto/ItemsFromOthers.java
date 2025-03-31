package app.web.dto;

import app.item.model.Category;
import app.item.model.Gender;
import app.item.model.Period;
import app.item.model.SizeItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Data
public class ItemsFromOthers {

    @NotBlank
    @Size(min = 3, max = 20, message = "Name length must be between 3 and 20 characters!")
    private String name;

    @NotBlank
    @Size(min = 3, max = 25, message = "Description length must be between 3 and 25 characters!")
    private String description;

    @NotBlank(message = "Please enter a valid image URL!")
    @URL(message = "Please enter a valid image URL!")
    private String imageUrl;

    @NotNull(message = "You must select a type of gender!")
    private Gender gender;

    @NotNull(message = "You must select a size!")
    private SizeItem size;

    @NotNull(message = "You must select a type of category!")
    private Category category;

    @NotNull(message = "Please select a rental period type!")
    private Period period;

    @NotNull
    private BigDecimal price = BigDecimal.ONE;

    @NotNull
    private UUID ownerId;

}
