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


import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FavoriteServiceUTest {

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

        when(itemService.getItemById(itemId)).thenReturn(null);

        assertThrows(DomainException.class, () -> favoriteService.createFavoriteItem(createFavorite, user));

        verify(favoriteRepository, never()).save(any(Favorite.class));
    }
}
