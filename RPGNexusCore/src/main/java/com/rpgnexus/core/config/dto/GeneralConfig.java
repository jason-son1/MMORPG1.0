package com.rpgnexus.core.config.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * general.yml 파일에 매핑되는 설정 객체입니다.
 */
@Data
@NoArgsConstructor
public class GeneralConfig {

    private String language = "ko-KR";
    private boolean debugMode = false;
    private ServerSettings serverSettings = new ServerSettings();

    @Data

    public static class ServerSettings {
        private String serverName = "RPGNexus";
        private int maxLevel = 100;
        private Map<String, String> messages = new HashMap<>();

        public ServerSettings() {
            messages.put("prefix", "&8[&6RPG&8] &f");
            messages.put("no-permission", "&c권한이 없습니다.");
        }
    }
}
