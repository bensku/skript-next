package io.github.bensku.skript.compiler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import io.github.bensku.skript.compiler.node.ConstantNode;
import io.github.bensku.skript.compiler.node.ExecutableNode;
import io.github.bensku.skript.compiler.node.Node;
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
        Method callTarget = info.getCallTarget();
        Object instance;
        try {
            instance = callTarget.getDeclaringClass().getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            SneakyThrow.sneakyThrow(e);
            assert false;
            return null;
        }
        
        // Compile child expressions
        Node[] children = new Node[callTarget.getParameterCount()];
        Parameter[] params = callTarget.getParameters();
        boolean foldable = info.isConstantFoldable(); // Assume constant-foldable if allowed
        for (int i = 0; i < children.length; i++) {
            Node child = compile(node.getChildren()[i]);
            children[i] = child;
            if (child instanceof ExecutableNode) {
                foldable = false; // Can't fold with non-constant children
            }
            
            // Convert single parameters to arrays as needed
            if (params[i].getType().isArray() && !child.getReturnType().isArray()) {
                
            }
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
