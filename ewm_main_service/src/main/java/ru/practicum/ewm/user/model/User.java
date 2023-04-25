package ru.practicum.ewm.user.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "users")
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
}
