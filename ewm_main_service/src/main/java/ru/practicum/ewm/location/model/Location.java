package ru.practicum.ewm.location.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "locations")
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
@ToString
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Float lat;
    private Float lon;
}
