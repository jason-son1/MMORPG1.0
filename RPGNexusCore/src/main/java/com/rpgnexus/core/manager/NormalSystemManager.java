package com.rpgnexus.core.manager;

import com.rpgnexus.core.RPGNexusCore;
import com.rpgnexus.core.config.dto.NormalSystemConfig;
import com.rpgnexus.core.config.dto.NormalSystemConfig.SystemDef;
import com.rpgnexus.core.data.dto.NexusProfile;
import com.rpgnexus.core.script.FormulaContext;
import com.rpgnexus.core.script.FormulaParser;
import com.rpgnexus.core.script.FormulaParser.Node;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages normal systems like Health, Mana, and Exp.
 * Calculates max stats based on attributes and level.
 */
public class NormalSystemManager extends Manager {

    private final FormulaParser parser = new FormulaParser();
    // Cache parsed nodes to avoid re-parsing every time
    // SystemName -> Section(Tools/Calc) -> Key -> Node
    private final Map<String, Map<String, Map<String, Node>>> formulaCache = new HashMap<>();

    public NormalSystemManager(RPGNexusCore plugin) {
        super(plugin);
    }

    @Override
    public void enable() {
        // Pre-parse formulas if needed, or lazy load.
        // For now, let's lazy load.
    }

    @Override
    public void disable() {
        formulaCache.clear();
    }

    /**
     * Updates the player's derived stats (Max Health, Max Mana, etc.)
     * based on the configuration in normal-system.yml.
     */
    public void updatePlayerStats(NexusProfile profile) {
        NormalSystemConfig config = plugin.getCoreManager().getConfigManager().getNormalSystemConfig();
        if (config == null)
            return;

        if (config.getPlayerHealthSystem() != null)
            processSystem(profile, "Health", config.getPlayerHealthSystem());

        if (config.getPlayerManaSystem() != null)
            processSystem(profile, "Mana", config.getPlayerManaSystem());

        if (config.getPlayerExpSystem() != null)
            processSystem(profile, "Exp", config.getPlayerExpSystem());
    }

    private void processSystem(NexusProfile profile, String systemName, SystemDef systemDef) {
        FormulaContext context = new FormulaContext(profile, null); // No defender in normal system

        // 1. Definition Phase
        // In Normal System, definitions map internal vars to %player-status...%
        // We need to preload these into context if they are used as inputs.
        Map<String, String> definitions = systemDef.getDefinition();
        if (definitions != null) {
            for (Map.Entry<String, String> entry : definitions.entrySet()) {
                String varName = entry.getKey();
                String mapping = entry.getValue(); // e.g. %player-status#Level%

                // If mapping is a player-status reference, we can fetch it now or let
                // FormulaContext handle it using the mapped name?
                // FormulaContext resolves keys starting with "player-status#".
                // But the formula uses "level" or "str".
                // So we need to put "level" -> profile.getStat("Level") into context.

                Node node = getCachedNode(systemName, "Definition", varName, mapping);
                try {
                    double value = node.evaluate(context);
                    context.put(varName, value);
                } catch (Exception e) {
                    // Ignore or log error
                    context.put(varName, 0.0);
                }
            }
        }

        // 2. Tools Phase (Intermediate calculations)
        Map<String, String> tools = systemDef.getTools();
        if (tools != null) {
            for (Map.Entry<String, String> entry : tools.entrySet()) {
                String varName = entry.getKey();
                String formula = entry.getValue();

                Node node = getCachedNode(systemName, "Tools", varName, formula);
                try {
                    double value = node.evaluate(context);
                    context.put(varName, value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // 3. Calculate Phase (Final Output to Attributes)
        Map<String, String> calculate = systemDef.getCalculate();
        if (calculate != null) {
            for (Map.Entry<String, String> entry : calculate.entrySet()) {
                String targetVarName = entry.getKey(); // e.g. main-health-status-max
                String formula = entry.getValue();

                Node node = getCachedNode(systemName, "Calculate", targetVarName, formula);
                try {
                    double result = node.evaluate(context);

                    // Apply result to Profile
                    // We need to look up what 'targetVarName' maps to in Definitions
                    if (definitions != null && definitions.containsKey(targetVarName)) {
                        String mapping = definitions.get(targetVarName);
                        // mapping: %player-status#Max-Health%
                        if (mapping.startsWith("%player-status#") && mapping.endsWith("%")) {
                            String statName = mapping.substring(15, mapping.length() - 1);
                            // Set the stat!
                            profile.setStat(statName, result);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Node getCachedNode(String system, String section, String key, String expression) {
        return formulaCache
                .computeIfAbsent(system, k -> new HashMap<>())
                .computeIfAbsent(section, k -> new HashMap<>())
                .computeIfAbsent(key, k -> parser.parse(expression));
    }
}
