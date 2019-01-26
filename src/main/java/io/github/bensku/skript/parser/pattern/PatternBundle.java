package io.github.bensku.skript.parser.pattern;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections4.trie.PatriciaTrie;

/**
 * A bundle that contains patterns.
 *
 */
public class PatternBundle {
    
    private final Pattern[] patterns;
    
    private final PatriciaTrie<Pattern> starts;
    
    private final PatriciaTrie<Pattern> ends;
    
    private final int patriciaMaxLength;
    
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
    
    public Iterator<Pattern> getPatterns(String input) {
        String reversed = new StringBuilder(input).reverse().toString();
        int startLen = input.length() < patriciaMaxLength ? input.length() : patriciaMaxLength;
        
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
