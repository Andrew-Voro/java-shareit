package ru.practicum.shareit.booking.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Entity
@Table(name = "bookings", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Booking {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    Long id;
    @Column(name = "start_date")
    LocalDateTime start;
    @Column(name = "end_date")
    LocalDateTime end;
    //@Column(name = "item_id")
    @ManyToOne
    //@CollectionTable(name = "items", joinColumns = @JoinColumn(name = "id"))
    //Long item;
    Item item;
    //@Column(name = "booker_id")
    @ManyToOne
    //@CollectionTable(name = "users", joinColumns = @JoinColumn(name = "id"))
    //Long booker;
    @JoinColumn(name ="booker_id")
    User booker;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    Status status;

}
