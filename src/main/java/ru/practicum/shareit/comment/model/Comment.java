package ru.practicum.shareit.comment.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String text;
    //@Column(name = "item_id")
    @ManyToOne
    //@CollectionTable(name = "items", joinColumns = @JoinColumn(name = "id"))
            // Long item ;
    Item item;
    //@Column(name = "author_id")
    @ManyToOne
    // @CollectionTable(name = "users", joinColumns = @JoinColumn(name = "id"))
    //Long author;
    @JoinColumn(name = "author_id")
    User author;
    LocalDateTime created;
}
