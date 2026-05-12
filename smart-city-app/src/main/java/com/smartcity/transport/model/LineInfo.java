package com.smartcity.transport.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LineInfo {
    private String name;
    private String color;
    private String textColor;
    private String target;
    private Map<String, List<String>> timetable;
}
