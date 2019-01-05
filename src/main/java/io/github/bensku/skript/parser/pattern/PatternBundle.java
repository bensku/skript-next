package io.github.bensku.skript.parser.pattern;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A bundle that contains patterns.
 *
 */
public class PatternBundle {
        
    /**
     * Arrays of patterns by character codes of first characters of them.
     */
    private final Pattern[][] startChars;
    
    /**
     * Arrays of patterns by character codes of last characters of them.
     */
    private final Pattern[][] endChars;
    
    private final Pattern[] patterns;
    
    PatternBundle(List<Pattern> patterns) {
        this.startChars = new Pattern[256][];
        this.endChars = new Pattern[256][];
        this.patterns = patterns.toArray(new Pattern[patterns.size()]);
        computeChars();
    }
    
    private void computeChars() {
        // Fill in the shortcuts in lists
        @SuppressWarnings("unchecked")
        List<Pattern>[] startLists = new List[256];
        @SuppressWarnings("unchecked")
        List<Pattern>[] endLists = new List[256];
        for (Pattern pattern : patterns) {
            PatternPart first = pattern.getFirst();
            if (first instanceof PatternPart.Literal) {
                addShortcut(startLists, ((PatternPart.Literal) first).getText().codePointAt(0), pattern);
            }
            PatternPart last = pattern.getLast();
            if (last instanceof PatternPart.Literal) {
                String text = ((PatternPart.Literal) last).getText();
                // TODO improve unicode support, not all characters have length 1
                addShortcut(endLists, text.codePointAt(text.length() - 1), pattern);
            }
        }
        
        // Convert these lists to arrays
        listsToArrays(startLists, startChars);
        listsToArrays(endLists, endChars);
    }
    
    private static void addShortcut(List<Pattern>[] array, int c, Pattern pattern) {
        List<Pattern> patterns = array[c]; // Get patterns already mapped to character
        if (pattern == null) { // Nothing, initialize array
            patterns = new ArrayList<>();
            array[c] = patterns;
        }
        
        patterns.add(pattern);
    }
    
    private static void listsToArrays(List<Pattern>[] lists, Pattern[][] arrays) {
        for (int i = 0; i < lists.length; i++) {
            List<Pattern> patterns = lists[i];
            if (patterns != null) {
                arrays[i] = patterns.toArray(new Pattern[patterns.size()]);
            }
        }
    }
    
    public Iterator<Pattern> getPatterns(String input) {
        Pattern[] starts = startChars[input.codePointAt(0)];
        Pattern[] ends = endChars[input.codePointAt(input.length() - 1)];
        
        return new Iterator<Pattern>() {
            
            /**
             * Array we're currently iterating on.
             */
            private Pattern[] array = starts;
            
            /**
             * Index of element we last returned. -1 when we haven't returned
             * anything yet.
             */
            private int index = -1;
            
            @Override
            public boolean hasNext() {
                if (index < array.length - 1) {
                    return true;
                } else {
                    return array != patterns; // Reference equality intended
                }
            }

            @Override
            public Pattern next() {
                if (array == null) { // Next array
                    if (starts == null) {
                        if (ends == null) {
                            array = patterns;
                        } else {
                            array = ends;
                        }
                    } else if (ends == null) {
                        array = patterns;
                    }
                    index = -1;
                }
                index++;
                if (index == array.length) { // Next array
                    if (array == starts) {
                        array = ends;
                    } else if (array == ends) {
                        array = patterns;
                    }
                }
                return array[index];
            }
        };
    }
}
