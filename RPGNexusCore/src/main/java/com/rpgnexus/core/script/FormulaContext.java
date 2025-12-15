package com.rpgnexus.core.script;

import com.rpgnexus.core.data.dto.NexusProfile;
import java.util.HashMap;

/**
 * Dynamic Context for Formula Evaluation.
 * Resolves variables lazily, especially player statuses.
 */
public class FormulaContext extends HashMap<String, Double> {

    private final NexusProfile attacker;
    private final NexusProfile defender;

    public FormulaContext(NexusProfile attacker, NexusProfile defender) {
        this.attacker = attacker;
        this.defender = defender;
    }

    @Override
    public Double get(Object key) {
        String k = (String) key;
        if (super.containsKey(k)) {
            return super.get(k);
        }

        // Dynamic resolution logic
        if (k.startsWith("player-status#")) {
            return resolvePlayerStatus(k);
        }

        return 0.0;
    }

    private double resolvePlayerStatus(String key) {
        // Format: player-status#StatName or player-status#StatName#$attacker$
        String[] parts = key.split("#");
        String statName = parts.length > 1 ? parts[1] : "";
        String target = parts.length > 2 ? parts[2] : "$attacker$";

        NexusProfile profile = target.equals("$defender$") ? defender : attacker;
        if (profile == null)
            return 0.0;

        // Fetch stat from profile
        // This requires knowing how NexusProfile exposes stats.
        // For now, I'll assume a getStat(String name) method or similar.
        // I will update this method after inspecting NexusProfile.
        return profile.getStat(statName);
    }
}
