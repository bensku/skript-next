package io.github.bensku.skript.compiler;

import java.lang.reflect.Method;

public class ExpressionInfo {

    /**
     * The methods that execute this expression, in order of preference.
     * Arguments to them are parameters given to us.
     */
    private final Method[] callTargets;

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

    public ExpressionInfo(Method[] callTargets, boolean isConstant, boolean isConstantFoldable) {
        assert callTargets != null;
        assert callTargets.length > 0;
        assert validateCallTargets();
        assert isConstant ? isConstantFoldable : true;
        this.callTargets = callTargets;
        this.isConstant = isConstant;
        this.isConstantFoldable = isConstantFoldable;
    }
    
    private boolean validateCallTargets() {
        for (Method m : callTargets) {
            assert m.isAccessible();
        }
        return true;
    }

    public Method[] getCallTargets() {
        return callTargets;
    }

    public boolean isConstant() {
        return isConstant;
    }

    public boolean isConstantFoldable() {
        return isConstantFoldable;
    }

}
