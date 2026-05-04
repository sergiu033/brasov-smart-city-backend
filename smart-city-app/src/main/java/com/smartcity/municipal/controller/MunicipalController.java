package com.smartcity.municipal.controller;

import com.smartcity.common.api.ApiResponse;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/municipal")
public class MunicipalController {

    @GetMapping("/services")
    public ApiResponse<List<Map<String, String>>> services() {
        return ApiResponse.success(List.of(
                Map.of("name", "Programari ghiseu", "status", "planned"),
                Map.of("name", "Plata taxe locale", "status", "planned")),
                "Servicii disponibile.");
    }

    @GetMapping("/services/{serviceCode}")
    public ApiResponse<Map<String, String>> serviceDetails(@PathVariable String serviceCode) {
        return ApiResponse.success(Map.of(
                "code", serviceCode,
                "name", "Serviciu municipal",
                "status", "planned",
                "channel", "online"),
                "Detalii serviciu.");
    }
}
