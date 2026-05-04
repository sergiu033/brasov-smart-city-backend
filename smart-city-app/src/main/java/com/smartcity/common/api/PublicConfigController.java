package com.smartcity.common.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
public class PublicConfigController {

    @GetMapping("/config")
    public ApiResponse<Map<String, Object>> config() {
        return ApiResponse.success(Map.of(
                "appName", "Smart City App",
                "supportedRoles", List.of("CITIZEN", "TOURIST"),
                "defaultLanguage", "ro",
                "features", List.of(
                        "auth",
                        "transport",
                        "recommendations",
                        "events",
                        "parking",
                        "reports",
                        "municipal")),
                "Configuratie publica disponibila.");
    }
}
