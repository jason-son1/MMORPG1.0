package com.rpgnexus.core.script;

import com.rpgnexus.core.RPGNexusCore;
import com.rpgnexus.core.data.dto.NexusProfile;
import com.rpgnexus.core.manager.Manager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 수식 계산을 담당하는 엔진입니다.
 * FormulaParser를 사용하여 수식을 컴파일하고 캐싱합니다.
 */
public class ScriptEngine extends Manager {

    private final FormulaParser parser = new FormulaParser();
    private final Map<String, FormulaParser.Node> formulaCache = new ConcurrentHashMap<>();

    public ScriptEngine(RPGNexusCore plugin) {
        super(plugin);
    }

    @Override
    public void enable() {
        plugin.getLogger().info("NexusScriptEngine 활성화됨 (Custom Parser)");
    }

    @Override
    public void disable() {
        formulaCache.clear();
    }

    @Override
    public void reload() {
        formulaCache.clear();
    }

    /**
     * 수식을 계산합니다.
     * 
     * @param formula 수식 문자열 (예: "%attacker_str% * 2")
     * @param caster  시전자 (attacker)
     * @param target  대상 (defender)
     * @return 계산 결과
     */
    public double calculate(String formula, NexusProfile caster, NexusProfile target) {
        if (formula == null || formula.isEmpty())
            return 0.0;

        try {
            // 1. AST 가져오기 (캐시 확인)
            FormulaParser.Node root = formulaCache.computeIfAbsent(formula, parser::parse);

            // 2. 컨텍스트 구성
            Map<String, Double> context = buildContext(caster, target);

            // 3. 평가
            return root.evaluate(context);

        } catch (Exception e) {
            plugin.getLogger().warning("수식 계산 오류 [" + formula + "]: " + e.getMessage());
            return 0.0;
        }
    }

    /**
     * 컨텍스트 맵을 빌드합니다.
     * 변수명 규칙:
     * - attacker_Key (소문자, 언더바)
     * - defender_Key
     */
    private Map<String, Double> buildContext(NexusProfile caster, NexusProfile target) {
        Map<String, Double> context = new HashMap<>();

        if (caster != null) {
            // statContainer의 모든 키를 attacker_XXX 로 매핑
            caster.getStatContainer().forEach((k, v) -> context.put("attacker_" + k, v));
        }

        if (target != null) {
            target.getStatContainer().forEach((k, v) -> context.put("defender_" + k, v));
        }

        // 추가: Config의 path 기반 변수 매핑을 원한다면 여기서 처리
        // 현재 FormulaParser는 %var%를 지원하며, AttributeRegistry 키는 소문자+언더바(combat_ability)
        // 사용자는 %attacker_combat_ability% 처럼 사용해야 함.

        return context;
    }
}
