package io.github.bensku.skript.compiler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.function.Function;

import io.github.bensku.skript.compiler.node.ConstantNode;
import io.github.bensku.skript.compiler.node.ExecutableNode;
import io.github.bensku.skript.compiler.node.Node;
import io.github.bensku.skript.compiler.node.ValueToArrayNode;
import io.github.bensku.skript.parser.AstNode;
import io.github.bensku.skript.util.SneakyThrow;

/**
 * Compiles individual expressions from AST nodes to executable nodes.
 *
 */
public class ExpressionCompiler {
    
    /**
     * Expression infos by compiler ids of patterns.
     */
    private final ExpressionInfo[] infos;
    
    public ExpressionCompiler(ExpressionInfo... infos) {
        assert infos != null;
        this.infos = infos;
    }
    
    /**
     * Compiles an expression from AST to a node that can be executed.
     * Constant-folding is done 
     * @param node AST node.
     * @return Executable or constant node.
     */
    public Node compile(AstNode node) {
        assert node != null;
        ExpressionInfo info = infos[node.getPattern().getCompilerId()];
        Method[] callTargets = info.getCallTargets();
        Function<AstNode, Node> compilerHook = info.getCompilerHook();
        if (compilerHook != null) { // Apply compiler hook if it exists
            Node result = compilerHook.apply(node);
            if (result != null) { // When it applies, return what it returned
                return result;
            }
        }
        assert callTargets != null;
        assert callTargets.length > 0;
        
        Method callTarget = null;
        Node[] children = null;
        boolean foldable = info.isConstantFoldable(); // Assume constant-foldable if allowed
        for (Method target : callTargets) {
            // Compile child expressions
            children = new Node[target.getParameterCount()];
            Parameter[] params = target.getParameters();
            for (int i = 0; i < children.length; i++) {
                Node child = compile(node.getChildren()[i]);
                if (child instanceof ExecutableNode) {
                    foldable = false; // Can't fold with non-constant children
                }
                
                // Convert single parameters to arrays as needed
                Class<?> type = params[i].getType();
                if (type.isArray() && !child.getReturnType().isArray()) {
                    child = new ValueToArrayNode(child);
                }
                // Check that parameters are correct now
                if (!type.equals(child.getReturnType())) {
                    continue; // Try next call target
                }
                
                children[i] = child;
            }
            callTarget = target;
        }
        assert callTarget != null;
        
        Object instance;
        try {
            instance = callTarget.getDeclaringClass().getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            SneakyThrow.sneakyThrow(e);
            assert false;
            return null;
        }
        
        // Create the executable node
        ExecutableNode compiled = new ExecutableNode(instance, callTarget, children);
        
        // If this is constant, it MUST BE constant-foldable
        assert info.isConstant() ? foldable : true;
        
        // Constant-fold if able, otherwise just return an executable node
        if (foldable) {
            return new ConstantNode(compiled.execute());
        } else {
            return compiled;
        }
    }
}
