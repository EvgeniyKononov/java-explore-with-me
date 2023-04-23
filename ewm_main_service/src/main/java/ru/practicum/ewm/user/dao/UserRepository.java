package ru.practicum.ewm.user.dao;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User u order by u.id")
    List<User> findAllOrderById(PageRequest page);

    List<User> findAllByIdIn(List<Long> ids);

    void deleteUserById(Long id);

    Optional<User> findByName(String name);
}
