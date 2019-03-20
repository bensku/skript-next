package io.github.bensku.skript.compiler.node;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import io.github.bensku.skript.util.SneakyThrow;

public class ExecutableNode implements Node {
	
    /**
     * Instance of the class call target is to be called on.
     */
	private final Object instance;
	
	/**
	 * Method to call.
	 */
	private final Method callTarget;
	
	/**
	 * Nodes that will be executed with their return values as parameters
	 * to call target.
	 */
	private final Node[] children;
	
	public ExecutableNode(Object instance, Method callTarget, Node... children) {
	    assert instance != null;
	    assert callTarget != null;
	    assert children != null;
		this.instance = instance;
		this.callTarget = callTarget;
		this.children = children;
	}
	
	public Object getInstance() {
	    return instance;
	}

	public Method getCallTarget() {
		return callTarget;
	}

	public Node[] getChildren() {
		return children;
	}
	
	@Override
	public Object execute() {
		Object[] params = new Object[children.length];
		for (int i = 0; i < params.length; i++) {
			params[i] = children[i].execute();
		}
		try {
            return callTarget.invoke(instance, params);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            SneakyThrow.sneakyThrow(e);
            assert false;
            return null;
        }
	}
	
	@Override
	public Class<?> getReturnType() {
	    return callTarget.getReturnType();
	}

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ExecutableNode other = (ExecutableNode) obj;
        if (callTarget == null) {
            if (other.callTarget != null)
                return false;
        } else if (!callTarget.equals(other.callTarget))
            return false;
        if (!Arrays.equals(children, other.children))
            return false;
        if (instance == null) {
            if (other.instance != null)
                return false;
        } else if (!instance.equals(other.instance))
            return false;
        return true;
    }
	
}
