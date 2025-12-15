package com.rpgnexus.core.hook.impl;

import com.rpgnexus.core.hook.interfaces.EconomyHook;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Vault 플러그인을 이용한 경제 연동 구현체입니다.
 */
public class VaultEconomyHook implements EconomyHook {

    private Economy economy;
    private boolean isHooked = false;

    @Override
    public String getPluginName() {
        return "Vault";
    }

    @Override
    public void hook(Plugin plugin) {
        if (plugin == null || !plugin.isEnabled())
            return;

        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
            this.economy = rsp.getProvider();
            this.isHooked = true;
        }
    }

    @Override
    public boolean isHooked() {
        return isHooked && economy != null;
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        if (!isHooked)
            return false;
        return economy.hasAccount(player);
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        if (!isHooked)
            return 0.0;
        return economy.getBalance(player);
    }

    @Override
    public void deposit(OfflinePlayer player, double amount) {
        if (!isHooked)
            return;
        economy.depositPlayer(player, amount);
    }

    @Override
    public void withdraw(OfflinePlayer player, double amount) {
        if (!isHooked)
            return;
        economy.withdrawPlayer(player, amount);
    }
}
