package app.web.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
public class NotificationResponse {

    private UUID id;
    private String message;
    private Date createdAt;
    private boolean read;
}
