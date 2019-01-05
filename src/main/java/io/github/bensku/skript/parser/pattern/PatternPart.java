package io.github.bensku.skript.parser.pattern;

/**
 * A syntax part.
 *
 */
public class PatternPart {
    
    public static class Literal extends PatternPart {
        
        /**
         * Text we want to match.
         */
        private final String text;
        
        public Literal(String text) {
            assert text != null;
            assert text.length() > 0;
            this.text = text;
        }
        
        public String getText() {
            return text;
        }
    }
    
    public static class Expression extends PatternPart {
        
        /**
         * Acceptable types, in order of preference.
         */
        private final Class<?>[] types;
        
        /**
         * Whether leaving this expression out is acceptable or not.
         */
        private final boolean nullable;
        
        public Expression(Class<?>[] types, boolean nullable) {
            assert types != null;
            assert types.length > 0;
            this.types = types;
            this.nullable = nullable;
        }
        
        public Class<?>[] getTypes() {
            return types;
        }
        
        public boolean isNullable() {
            return nullable;
        }
    }
}
