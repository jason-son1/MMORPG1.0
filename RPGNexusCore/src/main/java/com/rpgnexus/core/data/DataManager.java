package com.rpgnexus.core.data;

import com.rpgnexus.core.RPGNexusCore;
import com.rpgnexus.core.data.dto.NexusProfile;
import com.rpgnexus.core.data.storage.DataStorage;
import com.rpgnexus.core.data.storage.JsonDataStorage;
import com.rpgnexus.core.manager.Manager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 플레이어 데이터를 로드/저장하고 캐시를 관리하는 매니저입니다.
 */
public class DataManager extends Manager implements Listener {

    private final Map<UUID, NexusProfile> profileCache = new ConcurrentHashMap<>();
    private DataStorage storage;

    public DataManager(RPGNexusCore plugin) {
        super(plugin);
    }

    /**
     * 데이터 매니저 초기화.
     * 데이터 저장소(DataStorage)를 초기화하고 이벤트 리스너를 등록합니다.
     */
    @Override
    public void enable() {
        // TODO: Config에서 저장소 타입 설정 읽어오기 가능
        this.storage = new JsonDataStorage(plugin);
        this.storage.initialize();
        plugin.getServer().getPluginManager().registerEvents(this, plugin); // 이벤트 리스너 등록

        // 리로드 시 온라인 플레이어 데이터 로드
        Bukkit.getOnlinePlayers().forEach(p -> loadPlayer(p.getUniqueId(), p.getName()));
    }

    /**
     * 플러그인 비활성화 시 처리.
     * 접속 중인 모든 플레이어의 데이터를 즉시 저장하고 리소스를 정리합니다.
     */
    @Override
    public void disable() {
        // 모든 캐시 저장
        for (NexusProfile profile : profileCache.values()) {
            savePlayerSync(profile); // 종료 시는 동기 저장 권장 (데이터 유실 방지)
        }
        profileCache.clear();
        if (storage != null) {
            storage.close();
        }
    }

    public NexusProfile getProfile(UUID uuid) {
        return profileCache.get(uuid);
    }

    // --- Data Logic ---

    private void loadPlayer(UUID uuid, String name) {
        storage.load(uuid).thenAccept(profile -> {
            if (profile == null) {
                // 신규 유저 생성
                profile = new NexusProfile(uuid, name);
            }
            // 이름 업데이트 (변경되었을 수 있음)
            profile.setPlayerName(name);
            profileCache.put(uuid, profile);
        });
    }

    private void savePlayer(UUID uuid) {
        NexusProfile profile = profileCache.get(uuid);
        if (profile != null) {
            storage.save(profile);
        }
    }

    private void savePlayerSync(NexusProfile profile) {
        // 동기 저장을 위해 join() 사용, 메인 스레드 멈춤 주의 (Disable 시에는 허용)
        storage.save(profile).join();
    }

    // --- Listeners ---

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        loadPlayer(event.getPlayer().getUniqueId(), event.getPlayer().getName());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        NexusProfile profile = profileCache.remove(uuid); // 캐시에서 제거하며 가져옴

        if (profile != null) {
            // 비동기로 데이터 저장 수행
            // 주의: 서버 종료 시에는 플러그인이 비활성화되므로 동기 저장이 안전할 수 있으나,
            // 일반적인 로그아웃 상황에서는 서버 렉 방지를 위해 비동기가 권장됨.
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                storage.save(profile);
            });
        }
    }
}
