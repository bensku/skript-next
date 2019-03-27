package io.github.bensku.skript.scope;

import io.github.bensku.skript.compiler.node.Node;

public class BasicBlock {
    
    /**
     * Condition of this basic block.
     */
    private final Node condition;
    
    /**
     * If this block is repeatable, condition is called again after execution.
     */
    private final boolean repeatable;
    
    /**
     * Nodes to execute.
     */
    private final Node[] nodes;
    
    public BasicBlock(Node condition, boolean repeatable, Node[] nodes) {
        this.condition = condition;
        this.repeatable = repeatable;
        this.nodes = nodes;
    }
    
    public void execute() {
        while (condition == null && (boolean) condition.execute()) {
            for (Node node : nodes) {
                node.execute();
            }
            if (!repeatable) {
                return;
            }
        }
    }

    public Node getCondition() {
        return condition;
    }

    public boolean isRepeatable() {
        return repeatable;
    }

    public Node[] getNodes() {
        return nodes;
    }
    
    
}
