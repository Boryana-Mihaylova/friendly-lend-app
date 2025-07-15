package app.favorite.service;


import app.exception.DomainException;
import app.favorite.model.Favorite;
import app.favorite.repository.FavoriteRepository;
import app.item.model.Item;
import app.item.service.ItemService;
import app.user.model.User;
import app.web.dto.CreateFavorite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;

    private final ItemService itemService;

    @Autowired
    public FavoriteService(FavoriteRepository favoriteRepository, ItemService itemService) {
        this.favoriteRepository = favoriteRepository;
        this.itemService = itemService;
    }




    public Favorite getById(UUID id) {
        return favoriteRepository.findById(id).orElseThrow(() -> new DomainException("Rating with id [%s] does not exist.".formatted(id)));
    }



    public Favorite createFavoriteItem(CreateFavorite createFavorite, User user) {

        Item item = itemService.getItemById(createFavorite.getItemId());

        List<Favorite> existingFavorites = favoriteRepository.findByOwnerId(user.getId());
        boolean alreadyExists = existingFavorites.stream()
                .anyMatch(fav -> fav.getItem().getId().equals(item.getId()));

        if (alreadyExists) {
            return null;
        }

        Favorite favorite = Favorite.builder()
                .name(createFavorite.getName())
                .imageUrl(createFavorite.getImageUrl())
                .owner(user)
                .item(item)
                .build();

        return favoriteRepository.save(favorite);
    }

    public List<Favorite> getFavoritesByUser(User user) {
        return favoriteRepository.findByOwnerId(user.getId());
    }

    public void removeFavoriteById(UUID id, User user) {
        Favorite favorite = favoriteRepository.findById(id)
                .orElseThrow(() -> new DomainException("Favorite with id [%s] does not exist.".formatted(id)));


        if (!favorite.getOwner().getId().equals(user.getId())) {
            throw new DomainException("You are not authorized to delete this favorite.");
        }

        favoriteRepository.delete(favorite);
    }

}
