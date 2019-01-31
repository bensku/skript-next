package io.github.bensku.skript.parser;

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
}
