package io.github.bensku.skript.parser.pattern;

import java.util.ArrayList;
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
    
    PatternBundle(List<Pattern> patterns) {
        this.patterns = patterns.toArray(new Pattern[patterns.size()]);
        this.starts = new PatriciaTrie<>();
        this.ends = new PatriciaTrie<>();
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
        return null;
//        Pattern[] starts = startChars[input.codePointAt(0)];
//        Pattern[] ends = endChars[input.codePointAt(input.length() - 1)];
//        
//        return new Iterator<Pattern>() {
//            
//            /**
//             * Array we're currently iterating on.
//             */
//            private Pattern[] array = starts;
//            
//            /**
//             * Index of element we last returned. -1 when we haven't returned
//             * anything yet.
//             */
//            private int index = -1;
//            
//            @Override
//            public boolean hasNext() {
//                if (index < array.length - 1) {
//                    return true;
//                } else {
//                    return array != patterns; // Reference equality intended
//                }
//            }
//
//            @Override
//            public Pattern next() {
//                if (array == null) { // Next array
//                    if (starts == null) {
//                        if (ends == null) {
//                            array = patterns;
//                        } else {
//                            array = ends;
//                        }
//                    } else if (ends == null) {
//                        array = patterns;
//                    }
//                    index = -1;
//                }
//                index++;
//                if (index == array.length) { // Next array
//                    if (array == starts) {
//                        array = ends;
//                    } else if (array == ends) {
//                        array = patterns;
//                    }
//                }
//                return array[index];
//            }
//        };
    }
}
