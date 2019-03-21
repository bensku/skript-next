package io.github.bensku.skript.scope;

import io.github.bensku.skript.compiler.node.Node;

public class BasicBlock {
    
    /**
     * Nodes to execute.
     */
    private final Node[] nodes;
    
    public BasicBlock(Node[] nodes) {
        this.nodes = nodes;
    }
    
    public void execute() {
        for (Node node : nodes) {
            node.execute();
        }
    }
}
