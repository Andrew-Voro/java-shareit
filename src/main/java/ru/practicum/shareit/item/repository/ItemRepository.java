package ru.practicum.shareit.item.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query( "select i  from Item as i left join i.owner as u where u.id = ?1 order by i.id")
    List<Item>findByOwnerOrderById(Long owner);


    @Query( "select u  from Item u  where lower(u.name)  LIKE  lower( concat('%',concat(?1, '%')))" +
            " or lower(u.description)  LIKE  lower( concat('%',concat(?1, '%'))) and u.available = true ")
    List<Item> searchByNameOrDescription(String text);

/*"SELECT f.id, f.film_name, f.description, f.releaseDate, f.duration, f.rate " +
        "FROM film f " +
        "INNER JOIN FilmDirectors fd ON f.id = fd.film_id " +
        "INNER JOIN Directors d ON d.id = fd.directors_id " +
        "WHERE  lower(d.name)  LIKE lower('%" + query + "%') " +
            "ORDER BY f.rate DESC";*/
/*"select it " +
           "from Item as it " +
           "join it.user as u " +
           "where u.lastName like concat(?1, '%') "*/


   /* List<Item> findByUserId(long userId);

    Item save(Item item);

    Item getItem(Long itemId);

    List<ItemDto> searchByNameOrDescription(String text);
*/





}
