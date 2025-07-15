package app.favorite;


import app.exception.DomainException;
import app.favorite.model.Favorite;
import app.favorite.repository.FavoriteRepository;
import app.favorite.service.FavoriteService;
import app.item.model.Item;
import app.item.service.ItemService;
import app.user.model.User;
import app.web.dto.CreateFavorite;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FavoriteServiceTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private FavoriteService favoriteService;

    @Test
    void givenMissingFavoriteFromDatabase_whenGetById_thenExceptionIsThrown() {
        UUID favoriteId = UUID.randomUUID();
        when(favoriteRepository.findById(favoriteId)).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> favoriteService.getById(favoriteId));
    }



    @Test
    void givenNewFavorite_whenCreateFavoriteItem_thenFavoriteIsSavedSuccessfully() {
        UUID itemId = UUID.randomUUID();
        User user = new User();
        user.setId(UUID.randomUUID());

        CreateFavorite createFavorite = CreateFavorite.builder()
                .name("Test Favorite")
                .imageUrl("http://test.com/image.jpg")
                .itemId(itemId)
                .build();

        Item item = new Item();
        item.setId(itemId);

        when(itemService.getItemById(itemId)).thenReturn(item);
        when(favoriteRepository.save(any(Favorite.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Favorite savedFavorite = favoriteService.createFavoriteItem(createFavorite, user);

        assertThat(savedFavorite).isNotNull();
        assertThat(savedFavorite.getName()).isEqualTo("Test Favorite");
        assertThat(savedFavorite.getOwner()).isEqualTo(user);
        verify(favoriteRepository, times(1)).save(any(Favorite.class));
    }

    @Test
    void givenInvalidItem_whenCreateFavoriteItem_thenExceptionIsThrown() {
        UUID itemId = UUID.randomUUID();
        User user = new User();

        CreateFavorite createFavorite = CreateFavorite.builder()
                .name("Test Favorite")
                .imageUrl("http://test.com/image.jpg")
                .itemId(itemId)
                .build();

        when(itemService.getItemById(itemId)).thenThrow(new DomainException("Item not found"));

        assertThrows(DomainException.class, () -> favoriteService.createFavoriteItem(createFavorite, user));

        verify(favoriteRepository, never()).save(any(Favorite.class));
    }

    @Test
    void givenUserWithFavorites_whenGetFavoritesByUser_thenReturnList() {
        User user = new User();
        UUID userId = UUID.randomUUID();
        user.setId(userId);

        Favorite favorite1 = new Favorite();
        Favorite favorite2 = new Favorite();

        when(favoriteRepository.findByOwnerId(userId)).thenReturn(List.of(favorite1, favorite2));

        List<Favorite> result = favoriteService.getFavoritesByUser(user);

        assertThat(result).hasSize(2);
        verify(favoriteRepository).findByOwnerId(userId);
    }

    @Test
    void givenValidFavoriteOwnedByUser_whenRemoveFavorite_thenFavoriteIsDeleted() {
        UUID favoriteId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        Favorite favorite = new Favorite();
        favorite.setId(favoriteId);
        favorite.setOwner(user);

        when(favoriteRepository.findById(favoriteId)).thenReturn(Optional.of(favorite));

        favoriteService.removeFavoriteById(favoriteId, user);

        verify(favoriteRepository).delete(favorite);
    }

    @Test
    void givenFavoriteNotOwnedByUser_whenRemoveFavorite_thenThrowException() {
        UUID favoriteId = UUID.randomUUID();
        UUID actualOwnerId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();

        User otherUser = new User();
        otherUser.setId(otherUserId);

        User actualOwner = new User();
        actualOwner.setId(actualOwnerId);

        Favorite favorite = new Favorite();
        favorite.setId(favoriteId);
        favorite.setOwner(actualOwner);

        when(favoriteRepository.findById(favoriteId)).thenReturn(Optional.of(favorite));

        assertThrows(DomainException.class, () -> favoriteService.removeFavoriteById(favoriteId, otherUser));

        verify(favoriteRepository, never()).delete(any());
    }
}
