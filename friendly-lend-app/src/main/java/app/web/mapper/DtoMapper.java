package app.web.mapper;

import app.user.model.User;
import app.web.dto.UserEditRequest;

import app.web.dto.UserProfilePage;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DtoMapper {

    // Test:
    public static UserEditRequest mapUserToUserEditRequest(User user) {

        return UserEditRequest.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build();
    }



    public static UserProfilePage mapUserToUserProfilePage(User user) {

        return UserProfilePage.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build();
    }


}
