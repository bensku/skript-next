package io.github.bensku.skript.parser.pattern;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PatternParser {

    private Map<String, Class<?>> classes;
    
    public PatternParser(Map<String, Class<?>> classes) {
        assert classes != null;
        this.classes = classes;
    }
    
    /**
     * A very simple stack that operates with ints only.
     */
    private static class IntStack {
        
        /**
         * Backing array of this stack.
         */
        private int[] ints;
        
        /**
         * Current position in the array.
         */
        private int pos;
        
        public IntStack(int capacity) {
            this.ints = new int[capacity];
            this.pos = 0;
        }
        
        public void push(int value) {
            if (pos == ints.length - 1)
                enlargeArray();
            ints[pos] = value;
            pos++;
        }
        
        public int pop() {
            pos--;
            return ints[pos];
        }
        
        public boolean isEmpty() {
            return pos == 0;
        }
        
        private void enlargeArray() {
            int[] newArray = new int[ints.length * 2];
            System.arraycopy(ints, 0, newArray, 0, ints.length);
            this.ints = newArray;
        }

        @SuppressWarnings("unused")
        public void clear() {
            pos = 0;
        }
    }
    
    public Set<Pattern> parsePatterns(String input) {
        Set<Pattern> versions = new HashSet<>();
        boolean simple = true; // Simple patterns are used as-is
        
        IntStack optionals = new IntStack(4);
        IntStack choices = new IntStack(4);
        for (int i = 0; i < input.length();) {
            int c = input.codePointAt(i);
            
            if (c == '[') { // Start optional part
                optionals.push(i);
                simple = false;
            } else if (c == '(') { // Start choice part
                choices.push(i);
                simple = false;
            } else if (c == ']') { // End optional part
                int start;
                try {
                    start = optionals.pop();
                } catch (ArrayIndexOutOfBoundsException e) {
                    throw tooManyBrackets(i, "]");
                }
                
                versions.addAll(parsePatterns(input.substring(0, start) + input.substring(start + 1, i) + input.substring(i + 1)));
                versions.addAll(parsePatterns(input.substring(0, start) + input.substring(i + 1)));
            } else if (c == ')') { // End choice part
                int start;
                try {
                    start = choices.pop();
                } catch (ArrayIndexOutOfBoundsException e) {
                    throw tooManyBrackets(i, ")");
                }
                int optionStart = start;
                int nested = 0;
                for (int j = start + 1; j < i;) {
                    c = input.codePointAt(j);
                    
                    if (c == '(' || c == '[') {
                        nested++;
                    } else if (c == ')' || c == ']') {
                        nested--;
                    } else if (c == '|' && nested == 0) {
                        versions.addAll(parsePatterns(input.substring(0, start) + input.substring(optionStart + 1, j) + input.substring(i + 1)));
                        optionStart = j; // Prepare for next option
                    }
                    
                    j += Character.charCount(c);
                }
                assert nested == 0;
                versions.addAll(parsePatterns(input.substring(0, start) + input.substring(optionStart + 1, i) + input.substring(i + 1)));
            }
            
            i += Character.charCount(c);
        }
        
        // Make sure all groups were closed
        if (!optionals.isEmpty() || !choices.isEmpty()) {
            int errorStart;
            if (!optionals.isEmpty())
                errorStart = optionals.pop();
            else
                errorStart = choices.pop();            
            throw notEnoughBrackets(errorStart, input.substring(errorStart, errorStart + 1));
        }

        // If this is a simple name, its needs to be added here
        // (all groups were added earlier)
        if (simple) {
            versions.add(createPattern(input));
        }
        
        return versions;
    }
    
    protected Pattern createPattern(String input) {
        Pattern.Builder builder = Pattern.builder();
        int exprStart = -1;
        int exprEnd = 0;
        
        StringBuilder literal = new StringBuilder();
        boolean whitespace = true; // Ignore whitespace at start
        for (int i = 0; i < input.length();) {
            int c = input.codePointAt(i);
            
            if (c == '%') {
                if (exprStart == -1) {
                    exprStart = i;
                    if (literal.length() != 0) {
                        builder.literal(literal.toString());
                        literal = new StringBuilder();
                    }
                } else {
                    exprEnd = i + 1;
                    String exprInput = input.substring(exprStart + 1, i);
                    exprStart = -1;
                    
                    String[] typeDefs = exprInput.split("/");
                    Class<?>[] types = new Class[typeDefs.length];
                    for (int j = 0; j < types.length; j++) {
                        String def = typeDefs[j];
                        Class<?> type = classes.get(def);
                        if (type == null) {
                            throw new IllegalArgumentException("no type registered with name '" + def + "'");
                        }
                        types[j] = type;
                    }
                    builder.expression(types);
                }
            } else { // Don't have double whitespace in literals
                if (Character.isWhitespace(c)) {
                    if (!whitespace && input.length() - 1 != i) {
                        literal.appendCodePoint(c);
                    }
                    whitespace = true;
                } else {
                    whitespace = false;
                    literal.appendCodePoint(c);
                }
            }
            
            i += Character.charCount(c);
        }
        
        // Last literal part, if it exists
        if (exprEnd != input.length()) {
            builder.literal(literal.toString());
        }
        
        if (exprStart != -1) {
            throw new IllegalArgumentException("'%' at " + exprStart + " is not closed");
        }
        return builder.build();
    }
    
    private static IllegalArgumentException tooManyBrackets(int pos, String bracket) {
        return new IllegalArgumentException("too many brackets: '" + bracket + "' at " + pos + " closes nothing");
    }
    
    private static IllegalArgumentException notEnoughBrackets(int pos, String bracket) {
        return new IllegalArgumentException("not enough brackets: '" + bracket + "' at " + pos + " is not closed");
    }
}
