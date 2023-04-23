package ru.practicum.ewm.request.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByRequesterId(Long userId);

    List<Request> findAllByEventId(Long eventId);

    @Query("select r from Request r where r.status = :status and r.event = :event")
    List<Request> getRequestByStatusIs(RequestStatus status, Event event);

    Optional<Request> findByRequesterIdAndEventId(Long requesterId, Long eventId);
}
