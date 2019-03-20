package io.github.bensku.skript.compiler.node;

import java.lang.reflect.Array;

public class ValueToArrayNode implements Node {
    
    /**
     * Node whose return value we need to convert to an array.
     */
    private final Node node;
    
    public ValueToArrayNode(Node node) {
        this.node = node;
    }
        
    @Override
    public Object execute() {
        Object value = node.execute();
        Object array = Array.newInstance(value.getClass(), 1);
        Array.set(array, 0, value);
        return array;
    }

    @Override
    public Class<?> getReturnType() {
        return node.getClass(); // TODO implement array stuff
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ValueToArrayNode)) {
            return false;
        }
        return node.equals(((ValueToArrayNode) o).node);
    }

}
