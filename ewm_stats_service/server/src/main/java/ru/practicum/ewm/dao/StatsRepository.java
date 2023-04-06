package ru.practicum.ewm.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.model.Stats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Stats, Long> {
   /*List<Stats> findAllByUriAndTimestampIsBetween(String uri, LocalDateTime start, LocalDateTime end);

    @Query("select s from Stats s where s.uri = :uri and s.timestamp between :start and :end group by s.ip")
    List<Stats> findAllByUriAndTimestampIsBetweenGroupByIp(String uri, LocalDateTime start, LocalDateTime end);
*/
    List<Stats> findAllByTimestampIsBetween(LocalDateTime start, LocalDateTime end);
    /*
    @Query("select s from Stats s where s.timestamp between :start and :end group by s.ip")
    List<Stats> findAllByTimestampIsBetweenGroupByIp(LocalDateTime start, LocalDateTime end);
*/
   /* @Query("select s from Stats s where s.uri in :uris and s.timestamp between :start and :end group by s.ip")
    List<Stats> findAllByUriAndTimestampIsBetweenGroupByIp(List<String> uris, LocalDateTime start, LocalDateTime end);*/
/*
 @Query("select s.ip, s.app, s.uri from Stats s where s.uri in :uris and s.timestamp between :start and :end group by s.ip, s.app, s.uri")
 List<Stats> findAllByUriAndTimestampIsBetweenGroupByIp(List<String> uris, LocalDateTime start, LocalDateTime end);*/
    @Query("select s from Stats s where s.uri in :uris and s.timestamp between :start and :end")
    List<Stats> findAllByUriAndTimestampIsBetween(List<String> uris, LocalDateTime start, LocalDateTime end);
}
