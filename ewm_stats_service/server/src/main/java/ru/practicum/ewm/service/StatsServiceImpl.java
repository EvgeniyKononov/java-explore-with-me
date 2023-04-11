package ru.practicum.ewm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dao.StatsRepository;
import ru.practicum.ewm.dto.RequestStatsDto;
import ru.practicum.ewm.dto.ResponseStatsDto;
import ru.practicum.ewm.mapper.StatsMapper;
import ru.practicum.ewm.model.Stats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Autowired
    public StatsServiceImpl(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    @Override
    public RequestStatsDto createStats(RequestStatsDto requestStatsDto) {
        Stats stats = StatsMapper.toEntity(requestStatsDto);
        return StatsMapper.toRequestDto(statsRepository.save(stats));

        //  return "Информация сохранена";
    }

    @Override
    public List<ResponseStatsDto> getStats(String startStr, String endStr, List<String> uris, Boolean unique) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime start = LocalDateTime.parse(startStr, format);
        LocalDateTime end = LocalDateTime.parse(endStr, format);
        if (Objects.nonNull(uris)) {
            return getStatsByUri(start, end, uris, unique);
        } else {
            return getStats(start, end, unique);
        }
    }

    private List<ResponseStatsDto> getStats(LocalDateTime start, LocalDateTime end, Boolean unique) {
        if (unique) {
            return getUniqueStats(start, end);
        } else {
            return getAllStats(start, end);
        }
    }

    private List<ResponseStatsDto> getStatsByUri(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (unique) {
            return getUniqueStatsByUri(start, end, uris);
        } else {
            return getAllStatsByUri(start, end, uris);
        }
    }

    private List<ResponseStatsDto> getUniqueStats(LocalDateTime start, LocalDateTime end) {
        List<Stats> statsList = statsRepository.findAllByTimestampIsBetween(start, end);
        statsList = getUniqueStats(statsList);
        return getResponseStatsDtos(statsList);
    }

    private List<ResponseStatsDto> getAllStats(LocalDateTime start, LocalDateTime end) {
        List<Stats> statsList = statsRepository.findAllByTimestampIsBetween(start, end);
        return getResponseStatsDtos(statsList);
    }

    private List<ResponseStatsDto> getUniqueStatsByUri(LocalDateTime start, LocalDateTime end, List<String> uris) {
        List<Stats> statsList = statsRepository.findAllByUriAndTimestampIsBetween(uris, start, end);
        statsList = getUniqueStats(statsList);
        return getResponseStatsDtos(statsList);
    }

    private List<ResponseStatsDto> getAllStatsByUri(LocalDateTime start, LocalDateTime end, List<String> uris) {
        List<Stats> statsList = statsRepository.findAllByUriAndTimestampIsBetween(uris, start, end);
        return getResponseStatsDtos(statsList);
    }

    private List<Stats> getUniqueStats(List<Stats> statsList) {
        List<Stats> uniqueStats = new ArrayList<>();
        List<String> ips = new ArrayList<>();
        for (Stats stats : statsList) {
            String ip = stats.getIp();
            if (!ips.contains(ip)) {
                uniqueStats.add(stats);
                ips.add(ip);
            }
        }
        return uniqueStats;
    }

    private List<ResponseStatsDto> getResponseStatsDtos(List<Stats> statsList) {
        List<ResponseStatsDto> response = new ArrayList<>();
        List<String> uris = new ArrayList<>();
        for (Stats stats : statsList) {
            String uri = stats.getUri();
            if (!uris.contains(uri)) {
                response.addAll(getStatByApp(statsList, uri));
                uris.add(uri);
            }
        }
        response.sort(Comparator.comparingInt(ResponseStatsDto::getHits).reversed());
        return response;
    }

    private List<ResponseStatsDto> getStatByApp(List<Stats> statsList, String uri) {
        List<ResponseStatsDto> statsByApp = new ArrayList<>();
        Map<String, Integer> counter = countByApp(statsList, uri);
        for (Map.Entry<String, Integer> entry : counter.entrySet()) {
            statsByApp.add(StatsMapper.toResponseDto(entry.getKey(), uri, entry.getValue()));
        }
        return statsByApp;
    }

    private Map<String, Integer> countByApp(List<Stats> statsList, String uri) {
        Map<String, Integer> counter = new HashMap<>();
        for (Stats stats : statsList) {
            if (Objects.equals(stats.getUri(), uri)) {
                String app = stats.getApp();
                if (counter.containsKey(app)) {
                    int count = counter.get(app);
                    counter.put(app, count + 1);
                } else {
                    counter.put(app, 1);
                }
            }
        }
        return counter;
    }
}
