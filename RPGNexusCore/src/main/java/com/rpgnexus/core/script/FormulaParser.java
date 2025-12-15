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
        private final char op;

        public BinaryOpNode(Node left, char op, Node right) {
            this.left = left;
            this.op = op;
            this.right = right;
        }

        @Override
        public double evaluate(Map<String, Double> context) {
            double l = left.evaluate(context);
            double r = right.evaluate(context);
            switch (op) {
                case '+':
                    return l + r;
                case '-':
                    return l - r;
                case '*':
                    return l * r;
                case '/':
                    return r == 0 ? 0 : l / r;
                case '>':
                    return l > r ? 1.0 : 0.0;
                case '<':
                    return l < r ? 1.0 : 0.0;
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
    // expression = term | expression `+` term | expression `-` term
    // term = factor | term `*` factor | term `/` factor
    // factor = `+` factor | `-` factor | `(` expression `)` | number | functionName
    // `(` ... `)` | `%` variable `%`

    private Node parseExpression() {
        Node x = parseTerm();
        for (;;) {
            if (eat('+'))
                x = new BinaryOpNode(x, '+', parseTerm()); // addition
            else if (eat('-'))
                x = new BinaryOpNode(x, '-', parseTerm()); // subtraction
            else if (eat('>'))
                x = new BinaryOpNode(x, '>', parseTerm()); // Logic GT
            else if (eat('<'))
                x = new BinaryOpNode(x, '<', parseTerm()); // Logic LT
            else
                return x;
        }
    }

    private Node parseTerm() {
        Node x = parseFactor();
        for (;;) {
            if (eat('*'))
                x = new BinaryOpNode(x, '*', parseFactor()); // multiplication
            else if (eat('/'))
                x = new BinaryOpNode(x, '/', parseFactor()); // division
            else
                return x;
        }
    }

    private Node parseFactor() {
        if (eat('+'))
            return parseFactor(); // unary plus
        if (eat('-'))
            return new BinaryOpNode(new ConstantNode(0), '-', parseFactor()); // unary minus

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
            while ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9'))
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
                x = new VariableNode(funcName); // No parens -> treat as var? Or error. For now variable.
            }
        } else {
            throw new RuntimeException("Unexpected: " + (char) ch);
        }
        return x;
    }
}
