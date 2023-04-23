package ru.practicum.ewm.request.service;

import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.dto.RequestStatusUpdateDto;
import ru.practicum.ewm.request.dto.RequestsByStatusDto;

import java.util.List;

public interface RequestService {
    RequestDto createRequest(Long userId, Long eventId);

    List<RequestDto> getRequestsByUserId(Long userId);

    RequestDto cancelRequest(Long userId, Long requestId);

    List<RequestDto> getRequestsByEventId(Long eventId);

    RequestsByStatusDto updateRequestsStatusByEvent(RequestStatusUpdateDto statusUpdateDto, Event event);
}
