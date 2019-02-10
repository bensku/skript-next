package io.github.bensku.skript.parser;

import java.util.Arrays;

import io.github.bensku.skript.parser.pattern.Pattern;

public class AstNode {

    /**
     * Matched pattern.
     */
    private final Pattern pattern;
    
    /**
     * Children of this node.
     */
    private final AstNode[] children;
    
    public AstNode(Pattern pattern, AstNode[] children) {
        this.pattern = pattern;
        this.children = children;
    }
    
    public Pattern getPattern() {
        return pattern;
    }
    
    public AstNode[] getChildren() {
        return children;
    }
    
    @Override
    public String toString() {
        return "AstNode{pattern=" + pattern + ",children=" + Arrays.toString(children) + "}";
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AstNode)) {
            return false;
        }
        AstNode node = (AstNode) o;
        return pattern.equals(node.pattern) && Arrays.equals(children, node.children);
    }
}
