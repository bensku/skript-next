package io.github.bensku.skript.parser;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.github.bensku.skript.bitcode.Instruction;
import io.github.bensku.skript.parser.pattern.Pattern;
import io.github.bensku.skript.parser.pattern.PatternBundle;

public class LineParser {
    
    private Map<Class<?>, PatternBundle> bundles;
    
    public void parse(String input, List<Instruction> instrs) {
        parse(void.class, input, 0, input.length(), instrs);
    }
    
    public void parse(Class<?> type, String input, int start, int end, List<Instruction> instrs) {
        PatternBundle patterns = bundles.get(type);
        Iterator<Pattern> it = patterns.getPatterns(input);
        while (it.hasNext()) {
            Pattern pattern = it.next();
            
            // If start or end are literals, make sure they match input
            // TODO
            
            while (true) {
                // Try to find rest of literal parts from input in right order
                // TODO
                
                // Try to parse the expression parts using recursion
                // TODO
                
                // Expression part parsing failed?
                // Try different permutation of literal placement, if one exists
                continue;
                
                // Everything failed? Remember this pattern for possibly error reports
                // TODO
                
                // ... and go to next expression
            }
        }
    }
}
