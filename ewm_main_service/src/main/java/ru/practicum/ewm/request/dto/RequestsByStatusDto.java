package ru.practicum.ewm.request.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class RequestsByStatusDto {
    List<RequestDto> confirmedRequests;
    List<RequestDto> rejectedRequests;
}
