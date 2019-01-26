package io.github.bensku.skript.parser.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import io.github.bensku.skript.parser.pattern.Pattern;
import io.github.bensku.skript.parser.pattern.PatternBundle;
import io.github.bensku.skript.parser.pattern.PatternPart;
import io.github.bensku.skript.parser.pattern.PatternRegistry;

public class PatternTests {

    @Test
    public void patternTest() {
        // Test pattern parts with malformed parameters
        assertThrows(AssertionError.class, () -> new PatternPart.Literal(null));
        assertThrows(AssertionError.class, () -> new PatternPart.Expression(null, true));
        
        // Test pattern creation and methods it has
        PatternPart[] parts = new PatternPart[] {new PatternPart.Literal("foo"),
                new PatternPart.Expression(new Class[] {Object.class}, true), new PatternPart.Literal("bar")};
        Pattern pattern = new Pattern(parts);
        assertEquals(parts[0], pattern.getFirst());
        assertEquals(parts[2], pattern.getLast());
        assertEquals(parts, pattern.getParts());
    }
    
    @Test
    public void registryTest() {
        PatternRegistry registry = new PatternRegistry();
        assertEquals(0, registry.compile().size());
        
        // Test adding a pattern to registry, then compiling it
        PatternPart[] parts = new PatternPart[] {new PatternPart.Literal("foo"),
                new PatternPart.Expression(new Class[] {Object.class}, true), new PatternPart.Literal("bar")};
        Pattern pattern = new Pattern(parts);
        registry.addSyntax(Object.class, pattern);
        assertEquals(1, registry.compile().size());
    }
    
    @Test
    public void bundleTest() {
        PatternRegistry registry = new PatternRegistry();        
        PatternPart[] parts = new PatternPart[] {new PatternPart.Literal("foo"),
                new PatternPart.Expression(new Class[] {Object.class}, true), new PatternPart.Literal("bar")};
        Pattern pattern = new Pattern(parts);
        registry.addSyntax(Object.class, pattern);
        
        PatternBundle bundle = registry.compile().get(Object.class);
        Iterator<Pattern> it = bundle.getPatterns("foo abc bar");
        assertTrue(it.hasNext());
        assertEquals(pattern, it.next());
    }
    
    @Test
    public void bundleTest2() {
        PatternRegistry registry = new PatternRegistry();
        for (int i = 0; i < 100; i++) {
            PatternPart[] parts = new PatternPart[] {new PatternPart.Literal(UUID.randomUUID().toString()),
                    new PatternPart.Expression(new Class[] {Object.class}, true), new PatternPart.Literal(UUID.randomUUID().toString())};
            Pattern pattern = new Pattern(parts);
            registry.addSyntax(Object.class, pattern);
        }
        
        PatternBundle bundle = registry.compile().get(Object.class);
        Iterator<Pattern> it = bundle.getPatterns("");
        while (it.hasNext()) {
            assertNotNull(it.next());
        }
    }
}
