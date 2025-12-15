package com.rpgnexus.core.network;

import com.rpgnexus.core.RPGNexusCore;
import com.rpgnexus.core.manager.Manager;
import com.rpgnexus.core.network.packet.Packet;
import com.rpgnexus.core.network.packet.TargetInfoPacket;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.bukkit.attribute.Attribute;

public class NetworkManager extends Manager implements PluginMessageListener {

    private static final String CHANNEL = "nexus:data";
    private BukkitTask heartbeatTask;

    public NetworkManager(RPGNexusCore plugin) {
        super(plugin);
    }

    @Override
    public void enable() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, CHANNEL);
        Bukkit.getMessenger().registerIncomingPluginChannel(plugin, CHANNEL, this);

        plugin.getServer().getPluginManager().registerEvents(new NetworkListener(plugin), plugin);

        startHeartbeat();
    }

    @Override
    public void disable() {
        if (heartbeatTask != null && !heartbeatTask.isCancelled()) {
            heartbeatTask.cancel();
        }
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(plugin);
        Bukkit.getMessenger().unregisterIncomingPluginChannel(plugin);
    }

    public void sendPacket(Player player, Packet packet) {
        if (player == null || !player.isOnline())
            return;
        player.sendPluginMessage(plugin, CHANNEL, packet.toBytes());
    }

    private void startHeartbeat() {
        // Bukkit API(엔티티 조회, 체력 확인 등)는 반드시 메인 스레드에서 실행되어야 하므로 비동기(Asynchronously)가 아닌
        // 동기 작업으로 스케줄링합니다.
        // 최적화를 위해 주기를 1초(20 ticks)로 설정했으나, 플레이어가 많다면 틱이 튀지 않도록 주의해야 합니다.
        this.heartbeatTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Entity target = getTarget(player);
                if (target instanceof LivingEntity livingTarget) {
                    boolean isEnemy = target instanceof Monster; // 간단한 적대적 몹 판별 로직

                    // 타겟 정보 패킷 생성
                    TargetInfoPacket packet = new TargetInfoPacket(
                            target.getEntityId(),
                            target.getName(),
                            livingTarget.getHealth(),
                            livingTarget.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(), // Deprecated 메서드 대신
                                                                                                // Attribute 사용
                            isEnemy);
                    sendPacket(player, packet);
                }
            }
        }, 0L, 20L); // 1초(20 ticks) 주기
    }

    private Entity getTarget(Player player) {
        // RayTrace (최대 거리 20)
        return player.getTargetEntity(20);
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
        if (!channel.equals(CHANNEL))
            return;
        // Client -> Server 패킷 처리 로직 (필요 시 구현)
    }
}
