package io.github.bensku.skript.parser.pattern;

import java.util.ArrayList;
import java.util.Collection;
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
        String reversed = new StringBuilder(input).reverse().toString();
        
        return new Iterator<Pattern>() {
            
            /**
             * Trie search failed.
             */
            private boolean trieFailed;
            
            /**
             * If we're iterating all patterns, index in array.
             */
            private int index;
            
            private String startStr = input.substring(0, 6);
            private String endStr = reversed.substring(0, 6);
            
            private Iterator<Pattern> startPatterns = starts.prefixMap(startStr).values().iterator();
            private Iterator<Pattern> endPatterns = ends.prefixMap(endStr).values().iterator();
            
            private boolean start = true;
            
            @Override
            public boolean hasNext() {
                return trieFailed && index < patterns.length;
            }

            @Override
            public Pattern next() {
                return null;
            }
        };
    }
}
