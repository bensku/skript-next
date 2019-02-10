package io.github.bensku.skript.parser.pattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Pattern {
    
    /**
     * A builder for a pattern.
     *
     */
    public static class Builder {
        
        private List<PatternPart> parts;
        
        private Builder() {
            this.parts = new ArrayList<>();
        }
        
        public Builder literal(String text) {
            parts.add(new PatternPart.Literal(text));
            return this;
        }
        
        public Builder expression(Class<?>... types) {
            parts.add(new PatternPart.Expression(types));
            return this;
        }
        
        public Pattern build() {
            return new Pattern(parts.toArray(new PatternPart[parts.size()]));
        }
    }
    
    /**
     * Creates a new pattern builder.
     * @return A pattern builder.
     */
    public static Builder builder() {
        return new Builder();
    }
    
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
    
    @Override
    public String toString() {
        return "Pattern{" + Arrays.toString(parts) + "}";
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pattern)) {
            return false;
        }
        return Arrays.equals(parts, ((Pattern) o).parts);
    }
}
