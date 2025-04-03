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

        if (item == null) {
            throw new DomainException("Item with id [%s] does not exist.".formatted(createFavorite.getItemId()));
        }


        Favorite favorite = Favorite.builder()
                .name(createFavorite.getName())
                .imageUrl(createFavorite.getImageUrl())
                .owner(user)
                .item(item)
                .build();

        return favoriteRepository.save(favorite);
    }


}
