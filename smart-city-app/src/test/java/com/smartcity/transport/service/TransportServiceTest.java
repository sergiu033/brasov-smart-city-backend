package com.smartcity.transport.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcity.transport.dto.RouteRequest;
import com.smartcity.transport.dto.RouteResponse;
import com.smartcity.transport.model.Station;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransportServiceTest {

    @Mock
    private ObjectMapper objectMapper;

    private TransportService transportService;

    @BeforeEach
    void setUp() throws IOException {
        transportService = new TransportService(objectMapper);
        org.springframework.test.util.ReflectionTestUtils.setField(transportService, "dataPath", "dummy.json");
        
        File dummyFile = new File("dummy.json");
        dummyFile.createNewFile();
        dummyFile.deleteOnExit();

        Station s1 = new Station();
        s1.setId("node/1");
        s1.setName("Station 1");
        s1.setLat(45.648);
        s1.setLon(25.605);
        s1.setLines(Map.of("4_1", createLineInfo("4", "Target")));

        Station s2 = new Station();
        s2.setId("node/2");
        s2.setName("Station 2");
        s2.setLat(45.654);
        s2.setLon(25.611);
        s2.setLines(Map.of("4_1", createLineInfo("4", "Target")));

        when(objectMapper.readValue(any(File.class), any(TypeReference.class)))
                .thenReturn(List.of(s1, s2));

        transportService.init();
    }

    private com.smartcity.transport.model.LineInfo createLineInfo(String name, String target) {
        com.smartcity.transport.model.LineInfo info = new com.smartcity.transport.model.LineInfo();
        info.setName(name);
        info.setTarget(target);
        return info;
    }

    @Test
    void testFindRoute() {
        RouteRequest request = new RouteRequest();
        request.setOriginLat(45.647);
        request.setOriginLon(25.604);
        request.setDestinationLat(45.655);
        request.setDestinationLon(25.612);

        RouteResponse response = transportService.findRoute(request);

        assertNotNull(response);
        assertFalse(response.getLegs().isEmpty());
        assertTrue(response.getLegs().stream().anyMatch(leg -> "BUS".equals(leg.getMode())));
        assertTrue(response.getLegs().stream().anyMatch(leg -> "WALK".equals(leg.getMode())));
        assertTrue(response.getDurationMinutes() > 0);
    }
}
