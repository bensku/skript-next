package io.github.bensku.skript.parser.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import io.github.bensku.skript.parser.AstNode;
import io.github.bensku.skript.parser.ExpressionParser;
import io.github.bensku.skript.parser.pattern.Pattern;
import io.github.bensku.skript.parser.pattern.PatternRegistry;

public class ExpressionParserTest {
    
    @Test
    public void noPatterns() {
        PatternRegistry registry = new PatternRegistry();
        ExpressionParser parser = new ExpressionParser(registry.compile());
        assertNull(parser.parse(Object.class, "test", 0, 4));
    }
    
    @Test
    public void oneLiteral() {
        PatternRegistry registry = new PatternRegistry()
                .addSyntax(Object.class, Pattern.builder().literal("test").build());
        ExpressionParser parser = new ExpressionParser(registry.compile());
        assertEquals(new AstNode(Pattern.builder().literal("test").build(), new AstNode[1]), parser.parse(Object.class, "test", 0, 4));
    }
}
