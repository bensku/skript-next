package io.github.bensku.skript.parser.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import io.github.bensku.skript.parser.pattern.Pattern;
import io.github.bensku.skript.parser.pattern.PatternParser;

public class PatternParserTests {

    @Test
    public void simpleLiteral() {
        PatternParser parser = new PatternParser(Collections.emptyMap());
        assertEquals(Collections.singleton(Pattern.builder().literal("test").build()),
                parser.parsePatterns("test"));
    }
    
    @Test
    public void complexLiteral() {
        PatternParser parser = new PatternParser(Collections.emptyMap());
        Set<Pattern> patterns = new HashSet<>();
        patterns.add(Pattern.builder().literal("test foo bar").build());
        patterns.add(Pattern.builder().literal("foo bar").build());
        patterns.add(Pattern.builder().literal("test bar").build());
        patterns.add(Pattern.builder().literal("bar").build());
        assertEquals(patterns, parser.parsePatterns("[test] [foo] bar"));
    }
    
    @Test
    public void simpleExpression() {
        Map<String, Class<?>> types = new HashMap<>();
        types.put("string", String.class);
        PatternParser parser = new PatternParser(types);
        Set<Pattern> patterns = new HashSet<>();
        patterns.add(Pattern.builder().expression(String.class).build());
        assertEquals(patterns, parser.parsePatterns("%string%"));
    }
}
