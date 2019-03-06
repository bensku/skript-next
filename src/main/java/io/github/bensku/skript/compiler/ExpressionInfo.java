package io.github.bensku.skript.compiler;

import java.lang.reflect.Method;

public class ExpressionInfo {

    /**
     * The method that executes this expression. Its parameters are child
     * expressions of this expression.
     */
    private final Method callTarget;

    /**
     * Whether this expression is a constant or not. Constant expressions are
     * executed compile-time.
     */
    private final boolean isConstant;

    /**
     * If child expressions of this expressions are constants or have been
     * constant-folded, this can be executed compile-time.
     */
    private final boolean isConstantFoldable;

    public ExpressionInfo(Method callTarget, boolean isConstant, boolean isConstantFoldable) {
        assert callTarget != null;
        assert callTarget.isAccessible();
        assert isConstant ? isConstantFoldable : true;
        this.callTarget = callTarget;
        this.isConstant = isConstant;
        this.isConstantFoldable = isConstantFoldable;
    }

    public Method getCallTarget() {
        return callTarget;
    }

    public boolean isConstant() {
        return isConstant;
    }

    public boolean isConstantFoldable() {
        return isConstantFoldable;
    }

}
