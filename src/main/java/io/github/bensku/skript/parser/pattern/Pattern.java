package io.github.bensku.skript.parser.pattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.bensku.skript.parser.pattern.PatternPart.Literal;

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
    
    /**
     * Id of this pattern for compiler.
     */
    private int compilerId;
    
    public Pattern(PatternPart[] parts) {
        assert parts != null;
        assert parts.length > 0;
        this.parts = parts;
        this.compilerId = -1;
        assert sanitizePattern();
    }
    
    private boolean sanitizePattern() {
        // Ensure that there are no two consecutive literal/expression parts
        if (parts.length > 1) {
	        boolean literal = parts[0] instanceof Literal;
	        for (int i = 1; i < parts.length; i++) {
	        	if (parts[i] instanceof Literal) {
	        		if (literal) {
	        			assert false : "two consecutive literals";
	        		}
	        		literal = true;
	        	} else {
	        		if (!literal) {
	        			assert false : "two consecutive expressions";
	        		}
	        		literal = false;
	        	}
	        }
        }
        return true;
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
    
    public int getCompilerId() {
    	assert compilerId != -1;
    	return compilerId;
    }
    
    public void setCompilerId(int id) {
    	assert compilerId == -1;
    	this.compilerId = id;
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
