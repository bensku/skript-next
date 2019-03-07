package io.github.bensku.skript.parser.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.github.bensku.skript.compiler.node.ConstantNode;
import io.github.bensku.skript.compiler.node.ExecutableNode;

public class NodeTests {

    @Test
    public void constantNode() {
        ConstantNode node = new ConstantNode("foo");
        assertEquals("foo", node.execute());
    }
    
    public Object testCallTarget(String first, String second) {
        return first + second;
    }
    
    @Test
    public void executableNode() throws NoSuchMethodException, SecurityException {
        ExecutableNode node = new ExecutableNode(new NodeTests(),
                getClass().getDeclaredMethod("testCallTarget", String.class, String.class),
                new ConstantNode("foo"), new ConstantNode("bar"));
        assertEquals("foobar", node.execute());
    }
}
