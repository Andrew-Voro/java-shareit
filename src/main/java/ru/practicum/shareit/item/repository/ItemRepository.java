package ru.practicum.shareit.item.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select i  from Item as i left join i.owner as u where u.id = ?1 order by i.id")
    List<Item> findByOwnerOrderById(Long owner);

    @Query("select u  from Item u  where lower(u.name)  LIKE  lower( concat('%',concat(?1, '%')))" +
            " or lower(u.description)  LIKE  lower( concat('%',concat(?1, '%'))) and u.available = true ")
    List<Item> searchByNameOrDescription(String text);
}
