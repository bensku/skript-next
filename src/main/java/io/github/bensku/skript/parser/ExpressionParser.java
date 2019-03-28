package io.github.bensku.skript.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.github.bensku.skript.parser.pattern.Pattern;
import io.github.bensku.skript.parser.pattern.PatternBundle;
import io.github.bensku.skript.parser.pattern.PatternPart;
import io.github.bensku.skript.util.StringUtils;

/**
 * Parses expressions to {@link AstNode}s. Nested expressions are parsed
 * recursively, but everything else is done iteratively.
 *
 */
public class ExpressionParser {
    
    /**
     * Expression parser configuration.
     *
     */
    public static class Config {
        
        public static class Builder {
            
        }
        
        /**
         * Requests the parser to create actual error messages in addition to
         * just not returning AST nodes. This may significantly slow down the
         * parser, so consider enabling it only once something fails to parse.
         */
        private boolean errorMessages;
    }
    
    /**
     * Pattern bundles by their return types.
     */
    private final Map<Class<?>, PatternBundle> bundles;
    
    public ExpressionParser(Map<Class<?>, PatternBundle> bundles) {
        this.bundles = bundles;
    }
    
    public AstNode parse(String input) {
        return parse(void.class, input, 0, input.length());
    }
    
    /**
     * Attempts to parse the given input using one of our patterns into an
     * AST node.
     * @param type Return type of the expression.
     * @param input Input string.
     * @param start Start in input.
     * @param end End in input.
     * @return AST node, or null if no pattern matched the input.
     */
    public AstNode parse(Class<?> type, String input, int start, int end) {
        PatternBundle patterns = bundles.get(type);
        if (patterns == null) { // No patterns for this type
            return null;
        }
        
        // Ignore whitespace at start and end
        start = StringUtils.trimStart(input, start);
        end = StringUtils.trimEnd(input, end);
        
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
            // Intentional reference equality; this is just an optional optimization
            if (first != last && last instanceof PatternPart.Expression) {
                String text = ((PatternPart.Literal) last).getText();
                if (!StringUtils.startsWith(input, start, end, text, 0, text.length())) {
                    continue; // Pattern does not match
                }
            }
            
            // Find all occurrences of pattern parts
            PatternPart[] parts = pattern.getParts();
            List<Integer>[] starts = getStarts(parts, input, start, end);
            if (starts == null) {
                continue; // Input is missing some literal parts
            }
            
            int[] permutation = new int[starts.length];
            
            // Try all permutations of literal parts (iteratively)
            while (true) {
                /**
                 * If the permutation doesn't break order of literal parts,
                 * it is considered sane and parsing expression parts will be
                 * attempted.
                 */
                boolean sane = true;
                
                // Make sure we have sane permutation; literal parts must be in order!
                // Also make sure we don't assign many parts to same place
                int prev = Integer.MIN_VALUE;
                boolean[] used = new boolean[end - start];
                for (int i = 0; i < permutation.length; i++) {
                    List<Integer> list = starts[i];
                    if (list == null) {
                        continue; // Ignore expression part
                    }
                    
                    // Make sure this comes after previous part
                    int current = list.get(permutation[i]);
                    if (current < prev) { // Out of order permutation
                        sane = false;
                        break;
                    }
                    
                    // Check that current index hasn't been used before
                    if (used[current]) { // Invalid permutation
                    	sane = false;
                    	break;
                    }
                    
                    // Looks usable thus far...
                    prev = current;
                    used[current] = true;
                }
                
                // Check that we don't have anything extra at start
                if (starts[0] != null) { // Literal must start immediately
                	if (starts[0].get(permutation[0]) != start) {
                		sane = false;
                	}
                }
                
                // If permutation is sane, try to parse expression parts
                if (sane) {
                    /**
                     * Whether parsing this permutation succeeded or not.
                     */
                    boolean success = true;
                    
                    /**
                     * Index on line where next expression part starts.
                     */
                    int next = start;
                    
                    /**
                     * This is filled with child nodes as they are parsed.
                     */
                    AstNode[] children = new AstNode[parts.length];
                    for (int i = 0; i < parts.length; i++) {
                        List<Integer> list = starts[i];
                        if (list == null) { // Expression
                            PatternPart.Expression part = (PatternPart.Expression) parts[i];
                            int exprEnd;
                            if (i + 1 == parts.length) { // Expression is last
                                exprEnd = end;
                            } else {
                                exprEnd = starts[i + 1].get(permutation[i + 1]);
                            }
                            
                            // Parse for different return types
                            boolean exprParsed = false;
                            for (Class<?> ret : part.getTypes()) {
                                AstNode node = parse(ret, input, next, exprEnd);
                                if (node == null && ret.isArray()) {
                                    node = parse(ret.getComponentType(), input, next, exprEnd);
                                }
                                if (node != null) {
                                    exprParsed = true; // Expression parsing ok
                                    children[i] = node; // Possible child node
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
                    
                    // Check if parsing all expression parts succeeded
                    // If it did, make sure there is nothing but whitespace at end
                    if (success && StringUtils.isWhitespace(input, next, end)) {
                        // Return node with this pattern and the children we parsed
                        return new AstNode(pattern, children);
                    }
                }
                
                // This permutation didn't match, try next one
                if (!permutate(permutation, starts)) {
                    break; // No more permutations, pattern doesn't match
                }
            }
        }
        
        // Nothing matched the input
        return null;
    }
    
    /**
     * Gets an array of lists with all possible starting indices for literal
     * parts of the pattern.
     * @param parts Parts of the pattern.
     * @param input Input to search parts from.
     * @param start Start in input.
     * @param end End in input.
     * @return If not all literal parts can be placed, null. Otherwise, an
     * array with length equal to amount of given parts. Array slots
     * corresponding to literal parts are lists that contain all possible
     * starting indices for the them. Other slots are nulls.
     */
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
                    return null; // Failed to find a part, pattern doesn't match
                } else {
                    starts[i] = placements;
                }
            } // else: leave null in starts
        }
        
        return starts;
    }
    
    /**
     * Attempts to mutate given permutation array to provide different indices
     * in given lists of start points.
     * @param permutation Array of indices to given lists. It will be mutated!
     * @param starts Array of lists that contain possible starting positions.
     * @return Whether mutation succeeded or not.
     */
    private boolean permutate(int[] permutation, List<Integer>[] starts) {
        /**
         * The part for which we're about to try different placement for.
         */
        int permutateTarget = 0;
        while (true) {
            List<Integer> list = starts[permutateTarget];
            if (list == null) { // Can't find new place for expression part
                permutateTarget++; // Next part
                continue;
            } else { // Literal part might be mutable
                if (permutation[permutateTarget] == list.size() - 1) { // This part can't be mutated more
                    permutation[permutateTarget] = 0;
                    permutateTarget++;
                    if (permutateTarget == permutation.length) { // ... and it is last part
                        return false; // Out of permutations
                    } else { // ... but we can just mutate next part
                        continue;
                    }
                } else { // Mutate this part
                    permutation[permutateTarget]++;
                    return true;
                }
            }
        }
    }
}
