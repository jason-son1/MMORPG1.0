package com.rpgnexus.core.hook.interfaces;

import com.rpgnexus.core.hook.HookProvider;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

/**
 * 경제 플러그인(Economy) 연동을 위한 인터페이스입니다.
 */
public interface EconomyHook extends HookProvider<Plugin> {

    boolean hasAccount(OfflinePlayer player);

    double getBalance(OfflinePlayer player);

    void deposit(OfflinePlayer player, double amount);

    void withdraw(OfflinePlayer player, double amount);
}
