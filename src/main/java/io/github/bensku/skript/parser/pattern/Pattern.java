package io.github.bensku.skript.parser.pattern;

public class Pattern {
    
    /**
     * Parts we need to match.
     */
    private final PatternPart[] parts;
    
    public Pattern(PatternPart[] parts) {
        assert parts != null;
        assert parts.length > 0;
        this.parts = parts;
    }
    
    public PatternPart[] getParts() {
        return parts;
    }
    
    public PatternPart getFirst() {
        return parts[0];
    }
    
    public PatternPart getLast() {
        return parts[parts.length - 1];
    }
}
