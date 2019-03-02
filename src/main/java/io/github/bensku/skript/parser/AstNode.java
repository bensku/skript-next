package io.github.bensku.skript.parser;

import java.util.Arrays;

import io.github.bensku.skript.parser.pattern.Pattern;

/**
 * Represents an abstract syntax tree node. It contains the pattern that was
 * matched to produce it, and an array of child nodes for all parts of that.
 * For literal parts, child nodes are always null. For expression parts, they
 * are nodes with patterns that matched our pattern's return type requirements
 * and the text written by user.
 *
 */
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
    	assert pattern != null;
    	assert children != null;
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
