package ru.practicum.ewm.location.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class LocationDto {
    private Float lat;
    private Float lon;
}
