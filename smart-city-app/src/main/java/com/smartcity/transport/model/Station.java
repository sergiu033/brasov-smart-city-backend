package com.smartcity.transport.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Station {
    private String id;
    private String name;
    private String displayName;
    private double lat;
    private double lon;
    private Map<String, LineInfo> lines;
}
