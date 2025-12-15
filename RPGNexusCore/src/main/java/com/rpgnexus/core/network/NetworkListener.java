package com.rpgnexus.core.network;

import com.rpgnexus.core.RPGNexusCore;
import com.rpgnexus.core.data.DataManager;
import com.rpgnexus.core.data.dto.NexusProfile;
import com.rpgnexus.core.network.packet.PlayerStatusPacket;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class NetworkListener implements Listener {

    private final RPGNexusCore plugin;
    private final DataManager dataManager;
    private final NetworkManager networkManager;

    public NetworkListener(RPGNexusCore plugin) {
        this.plugin = plugin;
        this.dataManager = plugin.getCoreManager().getDataManager();
        this.networkManager = plugin.getCoreManager().getNetworkManager();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInfoChange(PlayerExpChangeEvent event) {
        sendUpdate(event.getPlayer(), event.getAmount()); // Exp Update
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLevelChange(PlayerLevelChangeEvent event) {
        sendUpdate(event.getPlayer(), 0);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        // 접속 시 초기 데이터 전송 (약간의 딜레이 권장)
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            sendUpdate(event.getPlayer(), 0);
        }, 20L);
    }

    private void sendUpdate(org.bukkit.entity.Player player, int expDelta) {
        NexusProfile profile = dataManager.getProfile(player.getUniqueId());
        if (profile != null) {
            // TODO: Exp update logic in profile
            double currentExp = profile.getStat("experience");
            profile.setStat("experience", currentExp + expDelta);
            networkManager.sendPacket(player, new PlayerStatusPacket(profile));
        }
    }
}
