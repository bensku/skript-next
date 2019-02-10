package io.github.bensku.skript.parser.pattern;

import java.util.Arrays;

import org.apache.commons.math3.analysis.function.Exp;

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
        
        @Override
        public String toString() {
            return "Literal{\"" + text + "\"}";
        }
        
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Literal)) {
                return false;
            }
            return text.equals(((Literal) o).text);
        }
    }
    
    public static class Expression extends PatternPart {
        
        /**
         * Acceptable types, in order of preference.
         */
        private final Class<?>[] types;
        
        public Expression(Class<?>[] types) {
            assert types != null;
            assert types.length > 0;
            this.types = types;
        }
        
        public Class<?>[] getTypes() {
            return types;
        }
        
        @Override
        public String toString() {
            return "Expression{" + Arrays.toString(types) + "}";
        }
        
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Expression)) {
                return false;
            }
            return Arrays.equals(types, ((Expression) o).types);
        }
    }
}
