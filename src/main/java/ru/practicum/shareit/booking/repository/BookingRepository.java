package ru.practicum.shareit.booking.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select i  from Booking as i left join i.item as u left join u.owner as o  where o.id = ?1 order by i.id desc ")
    List<Booking> findByOwner(Long owner);

    @Query("select i  from Booking as i left join i.item as u left join u.owner as o  where o.id = ?1 order by i.id desc ")
    Page<Booking> findByOwnerPaged(Long userId, PageRequest page);

    @Query("select i  from Booking as i left join i.booker as u  where u.id = ?1 order by i.id desc")
    List<Booking> findByBooker(Long booker);

    @Query("select i  from Booking as i left join i.booker as u  where u.id = ?1 order by i.id desc")
    Page<Booking> findByBooker(Long userId, PageRequest page);

    List<Booking> findByBooker_IdAndStartIsAfterOrderByIdDesc(Long bookerId, LocalDateTime now);

    List<Booking> findByBooker_IdAndEndIsBeforeOrderByIdDesc(Long bookerId, LocalDateTime now);

    List<Booking> findByBooker_IdAndEndIsAfterAndStartIsBeforeOrderByIdAsc(Long bookerId, LocalDateTime now, LocalDateTime now1);

    @Query("select i  from Booking as i left join i.item as u left join u.owner as o  where o.id = ?1 and i.start > ?2 order by i.id desc ")
    List<Booking> findByOwnerAndStartIsAfter(Long owner, LocalDateTime now);

    @Query("select i  from Booking as i left join i.item as u left join u.owner as o  where o.id = ?1 and i.end < ?2 order by i.id desc")
    List<Booking> findByOwnerAndEndIsBefore(Long owner, LocalDateTime now);

    @Query("select i  from Booking as i left join i.item as u left join u.owner as o  where o.id = ?1 and i.end > ?2 and i.start <?2 order by i.id asc")
    List<Booking> findByOwner_IdAndEndIsAfterAndStartIsBeforeOrderByIdAsc(Long owner, LocalDateTime now);

    List<Booking> findByItem_Id(Long itemId);

    @Query("select i  from Booking as i left join i.item as u left join u.owner as o  where o.id = ?1 and i.status =?2 order by i.id desc ")
    List<Booking> findByOwnerAndStatus(Long userId, Status status);

    List<Booking> findByBooker_IdAndStatus(Long userId, Status status);

    Optional<List<Booking>> findByItem_IdAndBooker_idAndStatus(Long itemId, Long userId, Status status);
}