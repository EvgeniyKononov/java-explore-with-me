package ru.practicum.ewm.request.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class RequestDto {
    private String created;
    private Long event;
    private Long id;
    private Long requester;
    private String status;
}
