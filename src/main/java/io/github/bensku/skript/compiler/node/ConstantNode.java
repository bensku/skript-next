package io.github.bensku.skript.compiler.node;

public class ConstantNode implements Node {

    private final Object value;
    
    public ConstantNode(Object value) {
        this.value = value;
    }
    
    @Override
    public Object execute() {
        return value;
    }

}
