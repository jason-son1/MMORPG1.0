package com.rpgnexus.core.manager;

import com.rpgnexus.core.RPGNexusCore;

/**
 * 모든 매니저 클래스의 부모 클래스입니다.
 * 플러그인 생명주기와 연동되는 기본 메서드를 정의합니다.
 */
public abstract class Manager {

    protected final RPGNexusCore plugin;

    public Manager(RPGNexusCore plugin) {
        this.plugin = plugin;
    }

    /**
     * 매니저가 활성화될 때 호출됩니다.
     */
    public abstract void enable();

    /**
     * 매니저가 비활성화될 때 호출됩니다.
     */
    public abstract void disable();

    /**
     * 매니저의 설정을 리로드하거나 상태를 갱신할 때 호출됩니다.
     */
    public void reload() {
        // 기본적으로는 아무 동작도 하지 않음. 필요시 오버라이드.
    }
}
