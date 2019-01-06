package io.github.bensku.skript.bitcode;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Deque;

public class ExecInstr extends Instruction {
    
    /**
     * Target of execution.
     */
    private final Object target;
    
    /**
     * Method to execute.
     */
    private final MethodHandle invoker;
    
    /**
     * Amount of arguments to call invoker with.
     */
    private final int argCount;
    
    public ExecInstr(Object target, MethodHandles.Lookup lookup) {
        this.target = target;
        for (Method method : target.getClass().getDeclaredMethods()) {
            if (method.getName().equals("execute")) {
                try {
                    this.invoker = lookup.unreflect(method).asSpreader(Object[].class, method.getParameterCount());
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException("object not executable", e);
                }
                this.argCount = method.getParameterCount();
                break;
            }
        }
        throw new IllegalArgumentException("object is missing instance method execute(...)");
    }

    @Override
    public void run(Deque<Object> stack) {
        Object[] params = new Object[argCount];
        for (int i = argCount - 1; i >= 0; i--) {
            params[i] = stack.pop();
        }
        try {
            Object result = invoker.invokeExact(params);
            if (result != null) {
                stack.push(result);
            }
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "exec: " + target.getClass().getSimpleName();
    }

}
