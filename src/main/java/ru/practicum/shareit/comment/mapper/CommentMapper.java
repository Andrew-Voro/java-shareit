package ru.practicum.shareit.comment.mapper;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class CommentMapper {
    public static Comment toDtoComment(CommentDto commentDto, User user, Item item) {  //Long userId,
        Comment comment = new Comment();
        comment.setId(commentDto.getId());
        comment.setAuthor(user);
        comment.setCreated(commentDto.getCreated());
        comment.setItem(item);
        comment.setText(commentDto.getText());
        return comment;
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }


}
