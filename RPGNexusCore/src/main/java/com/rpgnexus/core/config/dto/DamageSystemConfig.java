package com.rpgnexus.core.config.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@NoArgsConstructor
public class DamageSystemConfig {

    @JsonProperty("Damage-System")
    private DamageSystem damageSystem;

    @Data
    @NoArgsConstructor
    public static class DamageSystem {
        @JsonProperty("Damage-Category")
        private Map<String, Map<String, CategoryDetail>> damageCategory; // Changed to dynamic map

        @JsonProperty("Definitions")
        private Map<String, String> definitions;

        @JsonProperty("Tools")
        private Map<String, String> tools;

        @JsonProperty("Calculate")
        private Map<String, Map<String, String>> calculate;
    }

    @Data
    @NoArgsConstructor
    public static class CategoryDetail {
        @JsonProperty("display-name")
        private String displayName;

        @JsonProperty("default-multiplier")
        private double defaultMultiplier;

        @JsonProperty("description")
        private String description;
    }
}
