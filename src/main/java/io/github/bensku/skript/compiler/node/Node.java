package io.github.bensku.skript.compiler.node;

public interface Node {
    
    Object execute();
    
    Class<?> getReturnType();
}
