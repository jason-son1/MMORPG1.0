package com.rpgnexus.core.command;

import com.rpgnexus.core.RPGNexusCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class NexusCommand implements CommandExecutor {

    private final RPGNexusCore plugin;

    public NexusCommand(RPGNexusCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        if (!sender.hasPermission("rpgnexus.admin")) {
            sender.sendMessage("§c권한이 없습니다.");
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            long start = System.currentTimeMillis();
            plugin.getCoreManager().reload();
            long elapsed = System.currentTimeMillis() - start;
            sender.sendMessage("§aRPGNexusCore 리로드 완료! (" + elapsed + "ms)");
            return true;
        }

        sender.sendMessage("§6RPGNexusCore v" + plugin.getPluginMeta().getVersion());
        sender.sendMessage("§7/nexus reload - 설정 및 모듈 리로드");
        return true;
    }
}
