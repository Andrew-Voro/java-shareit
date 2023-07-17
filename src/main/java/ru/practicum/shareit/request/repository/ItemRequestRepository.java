package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findByRequestor_idOrderByCreatedDesc(Long userId);

    @Query("select i  from ItemRequest as i  left join i.requestor as r where r.id <> ?1 order by i.created DESC ")
    List<ItemRequest> findAllPaged(Long userId, PageRequest page);

    @Query("select i  from ItemRequest as i  left join i.requestor as r where r.id <> ?1 order by i.created DESC ")
    List<ItemRequest> findAllIt(Long userId);
}
