package io.github.bensku.skript.bitcode;

import java.util.Deque;

public class LdcInstr extends Instruction {
    
    /**
     * Object to load to stack.
     */
    private final Object value;
    
    public LdcInstr(Object value) {
        this.value = value;
    }

    @Override
    public void run(Deque<Object> stack) {
        stack.push(value);
    }

    @Override
    public String getName() {
        return "ldc: " + value;
    }

}
