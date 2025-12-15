package com.rpgnexus.core.data.node;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 직업 계층 구조를 나타내는 노드 클래스입니다.
 */
@Getter
@Setter
public class ClassNode {

    private final String className; // 직업 내부 이름 (Key)
    private String displayName; // 표시 이름
    private int tier; // 티어 (1차, 2차, ...)

    private ClassNode parent; // 부모 직업 (null이면 1차 직업)
    private List<ClassNode> children = new ArrayList<>(); // 하위 전직 가능 직업들

    public ClassNode(String className, int tier) {
        this.className = className;
        this.tier = tier;
    }

    public void addChild(ClassNode child) {
        children.add(child);
        child.setParent(this);
    }

    public boolean hasChild(String childName) {
        for (ClassNode child : children) {
            if (child.getClassName().equalsIgnoreCase(childName)) {
                return true;
            }
        }
        return false;
    }
}
