package io.github.bensku.skript.compiler.node;

import io.github.bensku.skript.scope.BasicBlock;

/**
 * Enters a basic block.
 *
 */
public class EnterBlockNode implements Node {
    
    /**
     * Block to enter.
     */
    private BasicBlock block;
    
    public EnterBlockNode(BasicBlock block) {
        this.block = block;
    }
    
    @Override
    public Object execute() {
        block.execute();
        return null;
    }

    @Override
    public Class<?> getReturnType() {
        return void.class;
    }

}
