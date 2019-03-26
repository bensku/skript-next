package io.github.bensku.skript.parser.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

import io.github.bensku.skript.compiler.ExpressionInfo;
import io.github.bensku.skript.compiler.ExpressionCompiler;
import io.github.bensku.skript.compiler.node.ConstantNode;
import io.github.bensku.skript.compiler.node.ExecutableNode;
import io.github.bensku.skript.compiler.node.Node;
import io.github.bensku.skript.parser.AstNode;
import io.github.bensku.skript.parser.pattern.Pattern;

public class CompilerTests {

    public Object callNothing() {
        return "abc";
    }
    
    @Test
    public void constantNode() throws NoSuchMethodException, SecurityException {
        Method method = getClass().getDeclaredMethod("callNothing");
        method.setAccessible(true); // ???
        ExpressionCompiler compiler = new ExpressionCompiler(new ExpressionInfo(new Method[] {method}, true, true, null));
        Pattern pattern = Pattern.builder().literal("test").build();
        pattern.setCompilerId(0);
        AstNode node = new AstNode(pattern, new AstNode[1]);
        assertEquals(new ConstantNode("abc"), compiler.compile(node));
    }
    
    public Object callStrs(String first, String second) {
        return first + second;
    }
    
    @Test
    public void nonFoldableNode() throws NoSuchMethodException, SecurityException {
        Method method = getClass().getDeclaredMethod("callNothing");
        method.setAccessible(true); // ???
        ExpressionCompiler compiler = new ExpressionCompiler(new ExpressionInfo(new Method[] {method}, false, false, null));
        Pattern pattern = Pattern.builder().literal("test").build();
        pattern.setCompilerId(0);
        AstNode node = new AstNode(pattern, new AstNode[1]);
        ExecutableNode executable = (ExecutableNode) compiler.compile(node);
        assertEquals(new ExecutableNode(executable.getInstance(), method, new Node[0]), executable);
        assertEquals("abc", executable.execute());
    }
}
