package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.handler.exception.ObjectNotFoundException;
import ru.practicum.shareit.handler.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Transactional
    @Override
    public ItemRequestDto add(Long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("User not found"));
        try {
            itemRequestDto.getDescription().equals(null);
        } catch (Exception e) {
            throw new ValidationException("Description is null");
        }
        return RequestMapper.toRequestDto(itemRequestRepository.save(RequestMapper.toDtoRequest(itemRequestDto, user)));
    }

    @Transactional(readOnly = true)
    @Override
    public ItemRequestDto getRequest(Long userId, Long requestId) {
        userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("User not found"));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() -> new ObjectNotFoundException("Request not found"));
        ItemRequestDto itemRequestDto = RequestMapper.toRequestDto(itemRequest);
        return itemRequestDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> getOwn(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("User not found"));
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequestor_idOrderByCreatedDesc(userId);
        return itemRequests.stream().map(RequestMapper::toRequestDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> getAllPaged(Long userId, Long from, Long size) {
        PageRequest page = PageRequest.of(from.intValue() > 0 ? from.intValue() / size.intValue() : 0, size.intValue());
        return itemRequestRepository.findAllPaged(userId, page).stream()
                .map(RequestMapper::toRequestDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> getAll(Long userId) {
        return itemRequestRepository.findAllIt(userId).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

}
