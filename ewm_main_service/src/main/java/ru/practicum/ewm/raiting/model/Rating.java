package ru.practicum.ewm.raiting.model;

import lombok.*;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.model.User;

import javax.persistence.*;

@Entity
@Table(name = "ratings")
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
@ToString
@IdClass(RatingPK.class)
public class Rating {
    @Id
    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    private Event event;
    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User rater;
    @Enumerated(EnumType.STRING)
    private Rate rate;
}
