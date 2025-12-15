package com.rpgnexus.core.script;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 수식 파서 (Recursive Descent Parser)
 * 사칙연산, 괄호, 변수(%...%), 함수(if, max, min, random)를 지원합니다.
 */
public class FormulaParser {

    // --- AST Nodes ---
    public interface Node {
        double evaluate(Map<String, Double> context);
    }

    public static class ConstantNode implements Node {
        private final double value;

        public ConstantNode(double value) {
            this.value = value;
        }

        @Override
        public double evaluate(Map<String, Double> context) {
            return value;
        }
    }

    public static class VariableNode implements Node {
        private final String name;

        public VariableNode(String name) {
            this.name = name;
        }

        @Override
        public double evaluate(Map<String, Double> context) {
            return context.getOrDefault(name, 0.0);
        }
    }

    public static class BinaryOpNode implements Node {
        private final Node left, right;
        private final String op;

        public BinaryOpNode(Node left, String op, Node right) {
            this.left = left;
            this.op = op;
            this.right = right;
        }

        @Override
        public double evaluate(Map<String, Double> context) {
            double l = left.evaluate(context);
            // Short-circuit for OR
            if (op.equals("OR")) {
                if (l != 0.0)
                    return l; // Truthy
                return right.evaluate(context);
            }

            double r = right.evaluate(context);
            switch (op) {
                case "+":
                    return l + r;
                case "-":
                    return l - r;
                case "*":
                    return l * r;
                case "/":
                    return r == 0 ? 0 : l / r;
                case ">":
                    return l > r ? 1.0 : 0.0;
                case "<":
                    return l < r ? 1.0 : 0.0;
                case ">=":
                    return l >= r ? 1.0 : 0.0;
                case "<=":
                    return l <= r ? 1.0 : 0.0;
                case "==":
                    return l == r ? 1.0 : 0.0;
                case "!=":
                    return l != r ? 1.0 : 0.0;
                default:
                    return 0;
            }
        }
    }

    public static class FunctionNode implements Node {
        private final String name;
        private final List<Node> args;

        public FunctionNode(String name, List<Node> args) {
            this.name = name;
            this.args = args;
        }

        @Override
        public double evaluate(Map<String, Double> context) {
            String funcName = name.toLowerCase();

            // Argument count validation
            int size = args.size();
            switch (funcName) {
                case "max":
                case "min":
                case "random":
                    if (size < 2)
                        throw new RuntimeException("Function '" + name + "' requires 2 arguments.");
                    break;
                case "if":
                    if (size < 3)
                        throw new RuntimeException("Function '" + name + "' requires 3 arguments.");
                    break;
            }

            // Lazy evaluation for special functions
            if (funcName.equals("if")) {
                double condition = args.get(0).evaluate(context);
                // 1.0 (True) or 0.0 (False). We can treat != 0 as true for flexibility.
                if (condition != 0.0) {
                    return args.get(1).evaluate(context);
                } else {
                    return args.get(2).evaluate(context);
                }
            }

            // Eager evaluation for standard math functions
            double[] v = new double[size];
            for (int i = 0; i < size; i++)
                v[i] = args.get(i).evaluate(context);

            switch (funcName) {
                case "max":
                    return Math.max(v[0], v[1]);
                case "min":
                    return Math.min(v[0], v[1]);
                case "random":
                    return v[0] + Math.random() * (v[1] - v[0]); // random(min, max)
                default:
                    return 0; // Unknown function
            }
        }
    }

    // --- Parser State ---
    private String expression;
    private int pos = -1, ch;

    public Node parse(String expression) {
        this.expression = expression;
        this.pos = -1;
        nextChar();
        Node root = parseExpression();
        if (pos < expression.length())
            throw new RuntimeException("Unexpected: " + (char) ch);
        return root;
    }

    private void nextChar() {
        ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
    }

    private boolean eat(int charToEat) {
        while (ch == ' ')
            nextChar();
        if (ch == charToEat) {
            nextChar();
            return true;
        }
        return false;
    }

    // Grammar:
    // expression = logical_or
    // logical_or = equality { " OR " equality }
    // equality = relational { ("==" | "!=") relational }
    // relational = additive { (">" | "<" | ">=" | "<=") additive }
    // additive = multiplicative { ("+" | "-") multiplicative }
    // multiplicative = factor { ("*" | "/") factor }
    // factor = ...

    private Node parseExpression() {
        return parseLogicalOr();
    }

    private Node parseLogicalOr() {
        Node x = parseEquality();
        for (;;) {
            if (eatString(" OR ")) // Case-insensitive handling might be needed but assuming strict for now
                x = new BinaryOpNode(x, "OR", parseEquality());
            else
                return x;
        }
    }

    private Node parseEquality() {
        Node x = parseRelational();
        for (;;) {
            if (eatString("=="))
                x = new BinaryOpNode(x, "==", parseRelational());
            else if (eatString("!="))
                x = new BinaryOpNode(x, "!=", parseRelational());
            else
                return x;
        }
    }

    private Node parseRelational() {
        Node x = parseAdditive();
        for (;;) {
            if (eatString(">="))
                x = new BinaryOpNode(x, ">=", parseAdditive());
            else if (eatString("<="))
                x = new BinaryOpNode(x, "<=", parseAdditive());
            else if (eat('>'))
                x = new BinaryOpNode(x, ">", parseAdditive());
            else if (eat('<'))
                x = new BinaryOpNode(x, "<", parseAdditive());
            else
                return x;
        }
    }

    private Node parseAdditive() {
        Node x = parseMultiplicative();
        for (;;) {
            if (eat('+'))
                x = new BinaryOpNode(x, "+", parseMultiplicative()); // addition
            else if (eat('-'))
                x = new BinaryOpNode(x, "-", parseMultiplicative()); // subtraction
            else
                return x;
        }
    }

    private Node parseMultiplicative() {
        Node x = parseFactor();
        for (;;) {
            if (eat('*'))
                x = new BinaryOpNode(x, "*", parseFactor()); // multiplication
            else if (eat('/'))
                x = new BinaryOpNode(x, "/", parseFactor()); // division
            else
                return x;
        }
    }

    private Node parseFactor() {
        if (eat('+'))
            return parseFactor(); // unary plus
        if (eat('-'))
            return new BinaryOpNode(new ConstantNode(0), "-", parseFactor()); // unary minus

        Node x;
        int startPos = pos;
        if (eat('(')) { // parentheses
            x = parseExpression();
            eat(')');
        } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
            while ((ch >= '0' && ch <= '9') || ch == '.')
                nextChar();
            x = new ConstantNode(Double.parseDouble(expression.substring(startPos, pos)));
        } else if (eat('%')) { // variables (%stat_str%)
            startPos = pos; // skip first %
            while (ch != '%' && ch != -1)
                nextChar();
            String varName = expression.substring(startPos, pos);
            eat('%'); // eat closing %
            x = new VariableNode(varName);
        } else if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) { // functions
            while ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9') || ch == '_') // Allow
                                                                                                                  // underscore
                nextChar();
            String funcName = expression.substring(startPos, pos);
            if (eat('(')) {
                List<Node> args = new ArrayList<>();
                do {
                    args.add(parseExpression());
                } while (eat(','));
                eat(')');
                x = new FunctionNode(funcName, args);
            } else {
                x = new VariableNode(funcName); // No parens -> treat as var
            }
        } else {
            throw new RuntimeException("Unexpected: " + (char) ch);
        }
        return x;
    }

    private boolean eatString(String s) {
        int savedPos = pos;
        int savedCh = ch;
        // Skip whitespace before check
        while (ch == ' ')
            nextChar();

        for (int i = 0; i < s.length(); i++) {
            if (ch != s.charAt(i)) {
                // backtrack
                pos = savedPos;
                ch = savedCh;
                // Re-consume char at pos if needed?
                // Wait, nextChar() advances. if we reset pos, we need to call nextChar() to set
                // ch correctly?
                // Actually my nextChar implementation sets ch based on pos.
                // So if I reset pos to savedPos, I need to call `ch = expression.charAt(pos)`
                // basic logic.
                // but `nextChar` does `++pos`.
                // Let's rely on simple peek logic or strict backtracking.
                // My parser keeps `ch` as "current char at pos".
                // When we start `eatString`, `ch` is the current char. `pos` is index of `ch`.

                // Let's implement robust backtrack manually
                pos = savedPos;
                // restore ch
                ch = savedCh;
                return false;
            }
            nextChar();
        }
        return true;
    }
}
