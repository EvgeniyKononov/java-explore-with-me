package ru.practicum.ewm.user.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDto createUser(UserDto userDto);

    List<UserDto> getUsers(List<Long> ids, PageRequest page);

    void deleteUser(Long userId);

    Optional<User> findById(Long userId);
}
