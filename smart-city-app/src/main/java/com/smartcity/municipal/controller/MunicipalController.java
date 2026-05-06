package com.smartcity.municipal.controller;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/municipal")
public class MunicipalController {

    @GetMapping("/services")
    public ResponseEntity<List<Map<String, String>>> services() {
        return ResponseEntity.ok(List.of(
                Map.of("name", "Programari ghiseu", "status", "planned"),
                Map.of("name", "Plata taxe locale", "status", "planned")));
    }

    @GetMapping("/services/{serviceCode}")
    public ResponseEntity<Map<String, String>> serviceDetails(@PathVariable String serviceCode) {
        return ResponseEntity.ok(Map.of(
                "code", serviceCode,
                "name", "Serviciu municipal",
                "status", "planned",
                "channel", "online"));
    }
}
