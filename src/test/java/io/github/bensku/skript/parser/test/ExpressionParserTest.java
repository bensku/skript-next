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
        assertEquals(new AstNode(Pattern.builder().literal("test").build(),
        		new AstNode[1]), parser.parse(Object.class, "test", 0, 4));
    }
    
    @Test
    public void faultyLiteral() {
        PatternRegistry registry = new PatternRegistry()
                .addSyntax(Object.class, Pattern.builder().literal("test").build());
        ExpressionParser parser = new ExpressionParser(registry.compile());
        assertNull(parser.parse(Object.class, "foo test bar", 0, 4));
    }
    
    @Test
    public void simpleExpression() {
        PatternRegistry registry = new PatternRegistry()
                .addSyntax(Object.class, Pattern.builder().literal("foo").expression(String.class).literal("bar").build())
                .addSyntax(String.class, Pattern.builder().literal("teststr").build());
        ExpressionParser parser = new ExpressionParser(registry.compile());
        assertEquals(new AstNode(Pattern.builder().literal("foo").expression(String.class).literal("bar").build(),
        		new AstNode[] {null, new AstNode(Pattern.builder().literal("teststr").build(), new AstNode[1]), null}),
        		parser.parse(Object.class, "foo teststr bar", 0, 15));
    }
    
    @Test
    public void faultyExpression() {
        PatternRegistry registry = new PatternRegistry()
                .addSyntax(Object.class, Pattern.builder().literal("foo").expression(String.class).literal("bar").build())
                .addSyntax(String.class, Pattern.builder().literal("teststr").build());
        ExpressionParser parser = new ExpressionParser(registry.compile());
        assertNull(parser.parse(Object.class, "foo abc teststr bar", 0, 15));
    }
    
    @Test
    public void hardExpression1() {
    	// Challenge: correctly identify the literal part
        PatternRegistry registry = new PatternRegistry()
                .addSyntax(String.class, Pattern.builder().literal("testy").build())
                .addSyntax(Object.class, Pattern.builder().expression(String.class).literal("testy").build());
        ExpressionParser parser = new ExpressionParser(registry.compile());
        assertEquals(new AstNode(Pattern.builder().expression(String.class).literal("testy").build(),
        		new AstNode[] {new AstNode(Pattern.builder().literal("testy").build(), new AstNode[1]), null}),
        		parser.parse(Object.class, "testy testy", 0, 11));
    }
    
    @Test
    public void hardExpression2() {
    	// Challenge: do not assign a literal multiple times
        PatternRegistry registry = new PatternRegistry()
                .addSyntax(Object.class, Pattern.builder().literal("test").build());
        ExpressionParser parser = new ExpressionParser(registry.compile());
        assertNull(parser.parse(Object.class, "test test", 0, 9));
    }
}
