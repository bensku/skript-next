package io.github.bensku.skript.compiler.node;

import java.util.Objects;

public class ConstantNode implements Node {

    private final Object value;
    
    public ConstantNode(Object value) {
        this.value = value;
    }
    
    @Override
    public Object execute() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ConstantNode)) {
            return false;
        }
        return Objects.equals(value, ((ConstantNode) o).value);
    }
}
