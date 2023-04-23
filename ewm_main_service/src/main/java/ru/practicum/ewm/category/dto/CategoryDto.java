package ru.practicum.ewm.category.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class CategoryDto {
    private Long id;
    @NotNull
    private String name;
}
