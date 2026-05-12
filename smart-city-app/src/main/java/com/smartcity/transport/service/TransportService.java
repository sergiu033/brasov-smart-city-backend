package com.smartcity.transport.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcity.transport.dto.RouteLeg;
import com.smartcity.transport.dto.RouteRequest;
import com.smartcity.transport.dto.RouteResponse;
import com.smartcity.transport.model.Station;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransportService {

    private final ObjectMapper objectMapper;
    
    @Value("${app.transport.data-path}")
    private String dataPath;
    
    private List<Station> stations = new ArrayList<>();
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @PostConstruct
    public void init() {
        try {
            File file = new File(dataPath);
            if (file.exists()) {
                stations = objectMapper.readValue(file, new TypeReference<List<Station>>() {});
                log.info("Loaded {} stations from {}", stations.size(), dataPath);
            } else {
                log.warn("Transport data not found at {}", file.getAbsolutePath());
            }
        } catch (IOException e) {
            log.error("Failed to load transport data", e);
        }
    }

    public RouteResponse findRoute(RouteRequest request) {
        if (stations.isEmpty()) {
            return RouteResponse.builder()
                    .legs(Collections.emptyList())
                    .totalDistance(0)
                    .durationMinutes(0)
                    .build();
        }

        // Dijkstra implementation for multimodal pathfinding
        Map<String, Double> minTimes = new HashMap<>();
        Map<String, String> predecessors = new HashMap<>();
        Map<String, RouteLeg> edgeToPredecessor = new HashMap<>();
        
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingDouble(Node::getTime));
        
        String originId = "ORIGIN";
        String destId = "DESTINATION";
        LocalTime startTime = LocalTime.now();
        
        minTimes.put(originId, 0.0);
        pq.add(new Node(originId, 0.0, request.getOriginLat(), request.getOriginLon()));
        
        while (!pq.isEmpty()) {
            Node current = pq.poll();
            
            if (current.id.equals(destId)) break;
            if (current.time > minTimes.getOrDefault(current.id, Double.MAX_VALUE)) continue;
            
            // 1. From ORIGIN, walk to any station
            if (current.id.equals(originId)) {
                for (Station s : stations) {
                    double dist = calculateDistance(current.lat, current.lon, s.getLat(), s.getLon());
                    int walkTime = (int) (dist * 12); // 12 min/km
                    double totalTime = current.time + walkTime;
                    
                    if (totalTime < minTimes.getOrDefault(s.getId(), Double.MAX_VALUE)) {
                        minTimes.put(s.getId(), totalTime);
                        predecessors.put(s.getId(), current.id);
                        edgeToPredecessor.put(s.getId(), RouteLeg.builder()
                                .mode("WALK")
                                .instructions("Walk to " + s.getName())
                                .fromStop("Your Location")
                                .toStop(s.getName())
                                .distance(dist)
                                .durationMinutes(walkTime)
                                .departureTime(startTime.format(TIME_FORMATTER))
                                .arrivalTime(startTime.plusMinutes(walkTime).format(TIME_FORMATTER))
                                .build());
                        pq.add(new Node(s.getId(), totalTime, s.getLat(), s.getLon()));
                    }
                }
            } else {
                // 2. From a station:
                Station currentStation = findStationById(current.id);
                LocalTime currentTime = startTime.plusMinutes((long) current.time);
                
                // a) Walk to DESTINATION
                double distToDest = calculateDistance(current.lat, current.lon, request.getDestinationLat(), request.getDestinationLon());
                int walkToDestTime = (int) (distToDest * 12);
                double totalTimeAtDest = current.time + walkToDestTime;
                
                if (totalTimeAtDest < minTimes.getOrDefault(destId, Double.MAX_VALUE)) {
                    minTimes.put(destId, totalTimeAtDest);
                    predecessors.put(destId, current.id);
                    edgeToPredecessor.put(destId, RouteLeg.builder()
                            .mode("WALK")
                            .instructions("Walk to your destination")
                            .fromStop(currentStation.getName())
                            .toStop("Destination")
                            .distance(distToDest)
                            .durationMinutes(walkToDestTime)
                            .departureTime(currentTime.format(TIME_FORMATTER))
                            .arrivalTime(currentTime.plusMinutes(walkToDestTime).format(TIME_FORMATTER))
                            .build());
                    pq.add(new Node(destId, totalTimeAtDest, request.getDestinationLat(), request.getDestinationLon()));
                }
                
                // b) Take a BUS to another station
                for (Station nextStation : stations) {
                    if (nextStation.getId().equals(current.id)) continue;
                    
                    String sharedLine = findSharedLine(currentStation, nextStation);
                    if (sharedLine != null) {
                        double distBus = calculateDistance(current.lat, current.lon, nextStation.getLat(), nextStation.getLon());
                        int busTravelTime = (int) (distBus * 4); // ~15km/h
                        
                        // Simplified: next departure is always in 2 mins
                        int waitTime = 2;
                        double totalTimeWithBus = current.time + waitTime + busTravelTime;
                        
                        if (totalTimeWithBus < minTimes.getOrDefault(nextStation.getId(), Double.MAX_VALUE)) {
                            minTimes.put(nextStation.getId(), totalTimeWithBus);
                            predecessors.put(nextStation.getId(), current.id);
                            edgeToPredecessor.put(nextStation.getId(), RouteLeg.builder()
                                    .mode("BUS")
                                    .route(sharedLine)
                                    .instructions("Take Bus " + sharedLine + " to " + nextStation.getName())
                                    .fromStop(currentStation.getName())
                                    .toStop(nextStation.getName())
                                    .distance(distBus)
                                    .durationMinutes(busTravelTime)
                                    .departureTime(currentTime.plusMinutes(waitTime).format(TIME_FORMATTER))
                                    .arrivalTime(currentTime.plusMinutes(waitTime + busTravelTime).format(TIME_FORMATTER))
                                    .build());
                            pq.add(new Node(nextStation.getId(), totalTimeWithBus, nextStation.getLat(), nextStation.getLon()));
                        }
                    }
                }
            }
        }
        
        List<RouteLeg> legs = new ArrayList<>();
        String curr = destId;
        while (predecessors.containsKey(curr)) {
            legs.add(0, edgeToPredecessor.get(curr));
            curr = predecessors.get(curr);
        }
        
        if (legs.isEmpty()) {
            double directDist = calculateDistance(request.getOriginLat(), request.getOriginLon(), request.getDestinationLat(), request.getDestinationLon());
            int directTime = (int) (directDist * 12);
            legs.add(RouteLeg.builder()
                    .mode("WALK")
                    .instructions("Walk directly to destination")
                    .fromStop("Your Location")
                    .toStop("Destination")
                    .distance(directDist)
                    .durationMinutes(directTime)
                    .departureTime(startTime.format(TIME_FORMATTER))
                    .arrivalTime(startTime.plusMinutes(directTime).format(TIME_FORMATTER))
                    .build());
        }

        int totalTime = legs.stream().mapToInt(RouteLeg::getDurationMinutes).sum();
        double totalDist = legs.stream().mapToDouble(RouteLeg::getDistance).sum();

        return RouteResponse.builder()
                .legs(legs)
                .totalDistance(totalDist)
                .durationMinutes(totalTime)
                .build();
    }

    private Station findStationById(String id) {
        return stations.stream().filter(s -> s.getId().equals(id)).findFirst().orElse(null);
    }

    private String findSharedLine(Station s1, Station s2) {
        for (String l1Key : s1.getLines().keySet()) {
            String l1Name = s1.getLines().get(l1Key).getName();
            for (String l2Key : s2.getLines().keySet()) {
                if (l1Name.equals(s2.getLines().get(l2Key).getName())) {
                    return l1Name;
                }
            }
        }
        return null;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private static class Node {
        String id;
        double time;
        double lat;
        double lon;

        Node(String id, double time, double lat, double lon) {
            this.id = id;
            this.time = time;
            this.lat = lat;
            this.lon = lon;
        }

        double getTime() { return time; }
    }
}
