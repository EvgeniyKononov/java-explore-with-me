package ru.practicum.ewm.request.model;

import lombok.*;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private RequestStatus status;
    private LocalDateTime created;
    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    private Event event;
    @ManyToOne
    @JoinColumn(name = "requester_id", referencedColumnName = "id")
    private User requester;
}
