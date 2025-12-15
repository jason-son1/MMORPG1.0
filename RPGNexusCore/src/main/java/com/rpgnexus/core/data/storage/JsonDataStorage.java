package com.rpgnexus.core.data.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rpgnexus.core.RPGNexusCore;
import com.rpgnexus.core.data.dto.NexusProfile;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * JSON 파일 기반의 간단한 데이터 저장소 구현체입니다.
 */
public class JsonDataStorage implements DataStorage {

    private final RPGNexusCore plugin;
    private final ObjectMapper mapper;
    private final File userDataFolder;

    public JsonDataStorage(RPGNexusCore plugin) {
        this.plugin = plugin;
        this.mapper = new ObjectMapper(); // JSON Mapper
        this.userDataFolder = new File(plugin.getDataFolder(), "userdata");
    }

    @Override
    public void initialize() {
        if (!userDataFolder.exists()) {
            userDataFolder.mkdirs();
        }
    }

    @Override
    public void close() {
        // Nothing to close for file io
    }

    @Override
    public CompletableFuture<NexusProfile> load(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            File file = new File(userDataFolder, uuid.toString() + ".json");
            if (!file.exists()) {
                // Return null to indicate new player or handle in manager
                return null;
            }
            try {
                return mapper.readValue(file, NexusProfile.class);
            } catch (Exception e) {
                plugin.getLogger().severe("데이터 로드 실패: " + uuid);
                e.printStackTrace();
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<Void> save(NexusProfile profile) {
        return CompletableFuture.runAsync(() -> {
            try {
                File file = new File(userDataFolder, profile.getUuid() + ".json");
                mapper.writeValue(file, profile);
            } catch (Exception e) {
                plugin.getLogger().severe("데이터 저장 실패: " + profile.getUuid());
                e.printStackTrace();
            }
        });
    }
}
