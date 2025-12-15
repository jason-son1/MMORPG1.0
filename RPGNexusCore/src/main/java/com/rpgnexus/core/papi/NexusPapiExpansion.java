package com.rpgnexus.core.papi;

import com.rpgnexus.core.RPGNexusCore;
import com.rpgnexus.core.data.DataManager;
import com.rpgnexus.core.data.dto.NexusProfile;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * PlaceholderAPI 확장 클래스입니다.
 * %nexus_stat_str% 등의 변수를 지원합니다.
 */
public class NexusPapiExpansion extends PlaceholderExpansion {

    private final RPGNexusCore plugin;
    private final DataManager dataManager;

    public NexusPapiExpansion(RPGNexusCore plugin) {
        this.plugin = plugin;
        this.dataManager = plugin.getCoreManager().getDataManager();
    }

    @Override // PAPI가 로드될 때 호출
    public boolean canRegister() {
        return true;
    }

    @Override // 플러그인 로드 후 영구적으로 유지
    public boolean persist() {
        return true;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "nexus";
    }

    @Override
    public @NotNull String getAuthor() {
        return "User";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null)
            return null;

        NexusProfile profile = dataManager.getProfile(player.getUniqueId());
        // 오프라인 플레이어 지원의 경우 dataManager에서 로드 로직 필요하나, 우선 온라인 캐시 기준
        if (profile == null)
            return "Loading...";

        if (params.startsWith("stat_")) {
            String statName = params.substring(5).toUpperCase(); // stat_str -> STR
            return String.valueOf(profile.getStat(statName));
        }

        if (params.equalsIgnoreCase("class")) {
            String className = profile.getMeta("class");
            return className != null ? className : "None";
        }

        if (params.equalsIgnoreCase("level")) {
            return String.valueOf((int) profile.getStat("level"));
        }

        if (params.equalsIgnoreCase("mana")) {
            return String.format("%.1f", profile.getStat("mana"));
        }

        return null;
    }
}
