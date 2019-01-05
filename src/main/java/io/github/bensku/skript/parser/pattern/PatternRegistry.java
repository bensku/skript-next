package io.github.bensku.skript.parser.pattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A collection of Skript patterns.
 *
 */
public class PatternRegistry {
    
    /**
     * Patterns by their return types.
     */
    private final Map<Class<?>, List<Pattern>> patterns;
    
    public PatternRegistry() {
        this.patterns = new HashMap<>();
    }
    
    public void addSyntax(Class<?> returnType, Pattern pattern) {
        assert returnType != null;
        assert pattern != null;
        patterns.compute(returnType, (k, v) -> {
            if (v == null)
                v = new ArrayList<>();
            v.add(pattern);
            return v;
        });
    }
    
    public Map<Class<?>, PatternBundle> compile() {
        Map<Class<?>, PatternBundle> bundles = new HashMap<>(patterns.size());
        for (Map.Entry<Class<?>, List<Pattern>> entry : patterns.entrySet()) {
            bundles.put(entry.getKey(), new PatternBundle(entry.getValue()));
        }
        return bundles;
    }
}
