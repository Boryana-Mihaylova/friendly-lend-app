package app.web.dto;

import app.deliveryLocation.model.Location;
import app.deliveryLocation.model.Town;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class LocationEditRequest {

    @NotNull(message = "You must select a type of location!")
    private Location location;

    @NotNull(message = "You must select a town!")
    private Town town;


}
