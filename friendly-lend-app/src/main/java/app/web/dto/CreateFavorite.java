package app.web.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.util.UUID;


@Builder
@Data
public class CreateFavorite {


    @NotBlank
    private String name;


    @NotBlank
    @URL
    private String imageUrl;

    @NotNull
    private UUID itemId;


}
