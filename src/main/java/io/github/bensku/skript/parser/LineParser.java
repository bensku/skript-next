package io.github.bensku.skript.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.github.bensku.skript.bitcode.Instruction;
import io.github.bensku.skript.parser.pattern.Pattern;
import io.github.bensku.skript.parser.pattern.PatternBundle;
import io.github.bensku.skript.parser.pattern.PatternPart;
import io.github.bensku.skript.parser.pattern.PatternPart.Expression;
import io.github.bensku.skript.util.StringUtils;

public class LineParser {
    
    private Map<Class<?>, PatternBundle> bundles;
    
    public void parse(String input, List<Instruction> instrs) {
        parse(void.class, input, 0, input.length(), instrs);
    }
    
    public boolean parse(Class<?> type, String input, int start, int end, List<Instruction> instrs) {
        PatternBundle patterns = bundles.get(type);
        Iterator<Pattern> it = patterns.getPatterns(input);
        while (it.hasNext()) {
            Pattern pattern = it.next();
            
            // If start or end are literals, make sure they match input
            PatternPart first = pattern.getFirst();
            if (first instanceof PatternPart.Literal) {
                String text = ((PatternPart.Literal) first).getText();
                if (!StringUtils.startsWith(input, start, end, text, 0, text.length())) {
                    continue; // Pattern does not match
                }
            }
            PatternPart last = pattern.getLast();
            if (last instanceof PatternPart.Expression) {
                String text = ((PatternPart.Literal) last).getText();
                if (!StringUtils.startsWith(input, start, end, text, 0, text.length())) {
                    continue; // Pattern does not match
                }
            }
            
            PatternPart[] parts = pattern.getParts();
            List<Integer>[] starts = getStarts(parts, input, start, end);
            if (starts == null) {
                continue; // Input is missing some literal parts
            }
            
            int[] permutation = new int[starts.length];
            
            // Try all permutations of literal parts (iteratively)
            while (true) {
                // Make sure we have sane permutation
                int prev = Integer.MIN_VALUE;
                boolean sane = true;
                for (int i = 0; i < permutation.length; i++) {
                    List<Integer> list = starts[i];
                    if (list == null) {
                        continue; // Ignore expression part
                    }
                    int current = list.get(permutation[i]);
                    if (current < prev) { // Out of order permutation
                        sane = false;
                        break;
                    }
                }
                
                // If permutation is sane, try to parse expression parts
                if (sane) {
                    boolean success = true;
                    int next = start;
                    for (int i = 0; i < parts.length; i++) {
                        List<Integer> list = starts[i];
                        if (list == null) { // Expression
                            PatternPart.Expression part = (Expression) parts[i];
                            int exprEnd;
                            if (i + 1 == parts.length) { // Expression is last
                                exprEnd = end;
                            } else {
                                exprEnd = starts[i].get(permutation[i]);
                            }
                            
                            // Parse for different return types
                            boolean exprParsed = false;
                            for (Class<?> ret : part.getTypes()) {
                                if (parse(ret, input, next, exprEnd, instrs)) {
                                    exprParsed = true; // Expression parsing ok
                                }
                            }
                            
                            // If we didn't succeed, try next permutation
                            if (!exprParsed) {
                                success = false;
                                break;
                            }
                        } else { // Literal
                            next = starts[i].get(permutation[i]) + ((PatternPart.Literal) parts[i]).getText().length();
                        }
                    }
                    
                    if (success) {
                        // TODO emit instruction
                        return true; // Pattern match found!
                    }
                }
                
                // This permutation didn't match, try next one
                int permutateTarget = 0;
                while (permutation[permutateTarget]++ == starts[permutateTarget].size()) {
                    // To zero and mutate next in array
                    permutation[permutateTarget] = 0;
                    permutateTarget++;
                    if (permutateTarget == permutation.length) { // Pattern doesn't match
                        break;
                    }
                }
            }
        }
        
        // Nothing matched the input
        return false;
    }
    
    private List<Integer>[] getStarts(PatternPart[] parts, String input, int start, int end) {
        @SuppressWarnings("unchecked")
        List<Integer>[] starts = new List[parts.length];
        
        // Find all possible places of literal parts
        for (int i = 0; i < parts.length; i++) {
            PatternPart part = parts[i];
            if (part instanceof PatternPart.Literal) {
                String text = ((PatternPart.Literal) part).getText();
                int len = text.length();
                List<Integer> placements = new ArrayList<>();
                for (int j = start;;) {
                    int pos = input.indexOf(text, j);
                    if (pos == -1) {
                        break;
                    } else if (pos + len > end) {
                        break;
                    }
                    placements.add(pos);
                    j = pos + len;
                }
                if (placements.isEmpty()) {
                    return null; // Failed to find a part, pattern doesn't match+
                }
            } // else: leave null in starts
        }
        
        return starts;
    }
}
