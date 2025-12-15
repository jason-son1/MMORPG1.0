package com.rpgnexus.core.config.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@NoArgsConstructor
public class NormalSystemConfig {

    @JsonProperty("Player-Health-System")
    private SystemDef playerHealthSystem;

    @JsonProperty("Player-Mana-System")
    private SystemDef playerManaSystem;

    @JsonProperty("Player-Exp-System")
    private SystemDef playerExpSystem;

    @Data
    @NoArgsConstructor
    public static class SystemDef {
        @JsonProperty("Definition")
        private Map<String, String> definition;

        @JsonProperty("Tools")
        private Map<String, String> tools;

        @JsonProperty("Calculate")
        private Map<String, String> calculate;
    }
}
