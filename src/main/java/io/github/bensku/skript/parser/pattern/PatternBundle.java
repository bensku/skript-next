package io.github.bensku.skript.parser.pattern;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections4.trie.PatriciaTrie;

/**
 * A bundle that contains patterns.
 *
 */
public class PatternBundle {
    
    /**
     * ALL patterns.
     */
    private final Pattern[] patterns;
    
    /**
     * A trie with patterns from start to end.
     */
    private final PatriciaTrie<Pattern> starts;
    
    /**
     * A trie with patterns from end to start.
     */
    private final PatriciaTrie<Pattern> ends;
    
    /**
     * Maximum prefix/suffix length for trie queries.
     */
    private final int patriciaMaxLength;
    
    /**
     * Minimum prefix/suffix length for trie queries.
     */
    private final int patriciaMinLength;
    
    PatternBundle(List<Pattern> patterns) {
        this.patterns = patterns.toArray(new Pattern[patterns.size()]);
        this.starts = new PatriciaTrie<>();
        this.ends = new PatriciaTrie<>();
        this.patriciaMaxLength = 6;
        this.patriciaMinLength = 3;
        computeTries();
    }
    
    private void computeTries() {
        for (Pattern pattern : patterns) {
            PatternPart first = pattern.getFirst();
            if (first instanceof PatternPart.Literal) {
                starts.put(((PatternPart.Literal) first).getText(), pattern);
            }
            
            PatternPart last = pattern.getLast();
            if (last instanceof PatternPart.Literal) {
                String reversed = new StringBuilder(((PatternPart.Literal) last).getText()).reverse().toString();
                ends.put(reversed, pattern);
            }
        }
    }
    
    /**
     * Gets an iterator for all patterns. The order which they're provided will
     * be optimized based on input. Same patterns may be provided multiple
     * times, because avoiding this would not be efficient.
     * @param input Input to get patterns for.
     * @return Iterator for ALL patterns, provided in order from most likely to
     * least likely based on the given input.
     */
    public Iterator<Pattern> getPatterns(String input) {
        String reversed = new StringBuilder(input).reverse().toString();
        int startLen = input.length() < patriciaMaxLength ? input.length() : patriciaMaxLength;
        
        /*
         * Iterator switches between starts and ends when their iterators are
         * out of patterns. Finally, it falls back to going through all
         * patterns when both of them have reached minimum length.
         */
        return new Iterator<Pattern>() {
            
            /**
             * If we're iterating all patterns, index in array. Else, -1.
             */
            private int index = -1;
            
            private String startStr = input.substring(0, startLen);
            private String endStr = reversed.substring(0, startLen);
            
            private Iterator<Pattern> startPatterns = starts.prefixMap(startStr).values().iterator();
            private Iterator<Pattern> endPatterns = ends.prefixMap(endStr).values().iterator();
            
            private boolean start = true;
            
            @Override
            public boolean hasNext() {
                return index < patterns.length;
            }

            @Override
            public Pattern next() {
                if (index >= 0) { // Looping through all patterns
                    return patterns[index++];
                } else if (start) {
                    return startNext();
                } else {
                    return endNext();
                }
            }
            
            private Pattern startNext() {
                if (startPatterns.hasNext()) {
                    return startPatterns.next();
                } else {
                    start = false;
                    if (startStr.length() <= patriciaMinLength) {
                        startPatterns = null;
                        if (endPatterns == null) {
                            return endNext();
                        } else {
                            index = 0;
                            return patterns[index++];
                        }
                    } else {
                        startStr = startStr.substring(0, startStr.length() - 1);
                        startPatterns = starts.prefixMap(startStr).values().iterator();
                        return startNext();
                    }
                }
            }
            
            private Pattern endNext() {
                if (endPatterns.hasNext()) {
                    return endPatterns.next();
                } else {
                    start = true;
                    if (endStr.length() <= patriciaMinLength) {
                        endPatterns = null;
                        if (startPatterns == null) {
                            return startNext();
                        } else {
                            index = 0;
                            return patterns[index++];
                        }
                    } else {
                        endStr = endStr.substring(0, endStr.length() - 1);
                        endPatterns = ends.prefixMap(endStr).values().iterator();
                        return startNext();
                    }
                }
            }
        };
    }
}
