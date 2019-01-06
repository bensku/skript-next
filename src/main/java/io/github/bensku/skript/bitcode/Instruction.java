package io.github.bensku.skript.bitcode;

import java.util.Deque;

public abstract class Instruction {
    
    public abstract void run(Deque<Object> stack);

    public abstract String getName();
    
    @Override
    public String toString() {
        return getName();
    }
}
