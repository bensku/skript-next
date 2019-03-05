package io.github.bensku.skript.compiler;


/**
 * Compiles scripts from AST to an intermediate format (IR).
 *
 */
public class SkIrCompiler {
    
    /**
     * Expression infos by compiler ids of patterns.
     */
    private final ExpressionInfo[] infos;
    
    public SkIrCompiler(ExpressionInfo[] infos) {
        this.infos = infos;
    }

}
