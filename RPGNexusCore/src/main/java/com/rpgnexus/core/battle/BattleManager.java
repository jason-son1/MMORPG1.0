package com.rpgnexus.core.battle;

import com.rpgnexus.core.RPGNexusCore;
import com.rpgnexus.core.config.dto.DamageSystemConfig;
import com.rpgnexus.core.config.dto.DamageSystemConfig.CategoryDetail;
import com.rpgnexus.core.data.dto.NexusProfile;
import com.rpgnexus.core.manager.Manager;
import com.rpgnexus.core.script.FormulaContext;
import com.rpgnexus.core.script.FormulaParser;
import com.rpgnexus.core.script.FormulaParser.Node;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages battle systems and damage calculation.
 * Now support fully dynamic damage categories via Tags.
 */
public class BattleManager extends Manager {

    private CombatListener combatListener;
    private final FormulaParser parser = new FormulaParser();
    private final Map<String, Map<String, Node>> formulaCache = new HashMap<>();

    public BattleManager(RPGNexusCore plugin) {
        super(plugin);
    }

    @Override
    public void enable() {
        this.combatListener = new CombatListener(plugin);
        plugin.getServer().getPluginManager().registerEvents(this.combatListener, plugin);
        plugin.getLogger().info("Dynamic Battle System Enabled.");
        formulaCache.clear();
    }

    @Override
    public void disable() {
        formulaCache.clear();
    }

    @Override
    public void reload() {
        formulaCache.clear();
        plugin.getLogger().info("BattleManager formula cache cleared.");
    }

    /**
     * Calculates damage based on context tags.
     * 
     * @param tags Map of CategoryKey -> ValueKey (e.g. "Damage-Kind" -> "melee")
     */
    public double calculateDamage(NexusProfile attacker, NexusProfile defender, double damageMag,
            Map<String, String> tags) {
        DamageSystemConfig config = plugin.getCoreManager().getConfigManager().getDamageSystemConfig();
        if (config == null || config.getDamageSystem() == null) {
            return damageMag;
        }

        // 1. Resolve Multipliers from Categories using Tags
        double contextMultiplier = 1.0;
        Map<String, Map<String, CategoryDetail>> allCategories = config.getDamageSystem().getDamageCategory();

        if (allCategories != null && tags != null) {
            // Iterate over all defined categories in config (e.g. Damage-Kind, Damage-Sort)
            for (Map.Entry<String, Map<String, CategoryDetail>> catEntry : allCategories.entrySet()) {
                String categoryKey = catEntry.getKey(); // e.g. "Damage-Kind"
                Map<String, CategoryDetail> values = catEntry.getValue();

                // Check if our tags contain a choice for this category
                // Tags might use simplified keys or exact keys?
                // Assuming exact keys for now based on user request "Damage-Kind: melee"
                if (tags.containsKey(categoryKey)) {
                    String tagValue = tags.get(categoryKey); // e.g. "melee"
                    if (values.containsKey(tagValue)) {
                        contextMultiplier *= values.get(tagValue).getDefaultMultiplier();
                    }
                }
            }
        }

        // 2. Build Context
        FormulaContext context = new FormulaContext(attacker, defender);
        context.put("damage_mag", damageMag);
        context.put("context_multiplier", contextMultiplier);

        // 3. Definitions
        Map<String, String> definitions = config.getDamageSystem().getDefinitions();
        if (definitions != null) {
            for (Map.Entry<String, String> entry : definitions.entrySet()) {
                evaluateAndPut(context, "Definition", entry.getKey(), entry.getValue());
            }
        }

        // 4. Tools
        Map<String, String> tools = config.getDamageSystem().getTools();
        if (tools != null) {
            for (Map.Entry<String, String> entry : tools.entrySet()) {
                evaluateAndPut(context, "Tools", entry.getKey(), entry.getValue());
            }
        }

        // 5. Calculate (Mode selection)
        // Default Mode PVP, PVE based on defender presence
        String mode = (defender == null) ? "PVE" : "PVP";

        // Maybe allow overriding mode via Tags?
        if (tags != null && tags.containsKey("Mode")) {
            mode = tags.get("Mode");
        }

        Map<String, Map<String, String>> calculate = config.getDamageSystem().getCalculate();
        if (calculate != null && calculate.containsKey(mode)) {
            Map<String, String> formulas = calculate.get(mode);
            if (formulas.containsKey("Final_Damage")) {
                return evaluate(context, "Calculate", mode + "_Final_Damage", formulas.get("Final_Damage"));
            }
        }

        return damageMag;
    }

    private void evaluateAndPut(FormulaContext context, String section, String key, String expression) {
        try {
            double val = evaluate(context, section, key, expression);
            context.put(key, val);
        } catch (Exception e) {
            context.put(key, 0.0);
        }
    }

    private double evaluate(FormulaContext context, String section, String key, String expression) {
        Node node = formulaCache
                .computeIfAbsent(section, k -> new HashMap<>())
                .computeIfAbsent(key, k -> parser.parse(expression));
        return node.evaluate(context);
    }
}
