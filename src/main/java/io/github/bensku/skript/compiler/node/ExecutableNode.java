package io.github.bensku.skript.compiler.node;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import io.github.bensku.skript.util.SneakyThrow;

public class ExecutableNode implements Node {
	
	private final Object instance;
	
	private final Method callTarget;
	
	private final Node[] children;
	
	public ExecutableNode(Object instance, Method callTarget, Node[] children) {
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
}
