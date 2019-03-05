package io.github.bensku.skript.compiler;

import java.lang.reflect.Method;

public class ExpressionInfo {
    
    /**
     * The method that executes this expression. Its parameters are child
     * expressions of this expression.
     */
    private final Method callTarget;
    
    /**
     * Whether this expression is a literal or not. Literal expressions are
     * executed compile-time instead of runtime.
     */
    private final boolean isLiteral;
    
    /**
     * If child expressions of this expression are all literals and/or have
     * been precomputed, this expression will be executed compile-time if
     * precomputing it is allowed.
     */
    private final boolean canBePrecomputed;
    
    public ExpressionInfo(Method callTarget, boolean isLiteral, boolean canBePrecomputed) {
        this.callTarget = callTarget;
        this.isLiteral = isLiteral;
        this.canBePrecomputed = canBePrecomputed;
    }

	public Method getCallTarget() {
		return callTarget;
	}

	public boolean isLiteral() {
		return isLiteral;
	}

	public boolean isCanBePrecomputed() {
		return canBePrecomputed;
	}
    
}
