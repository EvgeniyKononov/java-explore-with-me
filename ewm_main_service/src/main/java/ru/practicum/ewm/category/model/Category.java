package ru.practicum.ewm.category.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "categories")
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
@ToString
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
}
