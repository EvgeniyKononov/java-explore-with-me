package ru.practicum.ewm.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.model.Stats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Stats, Long> {

    List<Stats> findAllByTimestampIsBetween(LocalDateTime start, LocalDateTime end);

    @Query("select s from Stats s where s.uri in :uris and s.timestamp between :start and :end")
    List<Stats> findAllByUriAndTimestampIsBetween(List<String> uris, LocalDateTime start, LocalDateTime end);
}
