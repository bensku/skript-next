package io.github.bensku.skript.annotate;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.bensku.skript.compiler.ExpressionCompiler;
import io.github.bensku.skript.compiler.ExpressionInfo;
import io.github.bensku.skript.compiler.node.ConstantNode;
import io.github.bensku.skript.parser.ExpressionParser;
import io.github.bensku.skript.parser.pattern.PatternParser;
import io.github.bensku.skript.parser.pattern.PatternRegistry;
import io.github.bensku.skript.parser.pattern.ReturnType;

/**
 * Allows registration of types and expressions, and can create parsers and
 * compilers that understand them.
 *
 */
public class SkriptRegistry {
    
    /**
     * Types registered here. Mutates internals of {@link #patternParser}.
     */
    private final Map<String, Class<?>> types;
    
    /**
     * Pattern parser. New types are added by modifying {@link #types}.
     */
    private final PatternParser patternParser;
    
    /**
     * Contains all expression infos, in registration order. Passed to compiler
     * so that it knows how to compile patterns it receives.
     */
    private final List<ExpressionInfo> exprInfos;
    
    /**
     * Pattern registry where new patterns will be registered.
     */
    private final PatternRegistry patternRegistry;
    
    public SkriptRegistry() {
        this.types = new HashMap<>();
        this.patternParser = new PatternParser(types);
        this.exprInfos = new ArrayList<>();
        this.patternRegistry = new PatternRegistry();
    }
    
    /**
     * Registers a type with given name.
     * @param name Name for the type. This is used in patterns to refer to it.
     * @param type Actual type.
     */
    public void registerType(String name, Class<?> type) {
        if (types.containsKey(name)) {
            throw new IllegalArgumentException("cannot re-register type " + name);
        }
        types.put(name, type);
    }

    /**
     * Call target method and a priority, sortable.
     *
     */
    private static class TargetMethod implements Comparable<TargetMethod> {
        
        public final Method method;
        public final int priority;
        
        public TargetMethod(Method method, int priority) {
            this.method = method;
            this.priority = priority;
        }

        @Override
        public int compareTo(TargetMethod o) {
            return Integer.compare(priority, o.priority);
        }
    }
    
    /**
     * Registers an expression whose features are specified by annotations.
     * @param type Expression class, with necessary annotations declared.
     */
    public void register(Class<?> type) {
        Patterns patterns = type.getDeclaredAnnotation(Patterns.class);
        if (patterns == null) {
            throw new IllegalArgumentException("expression must define patterns");
        }
        ReturnType returnType = type.getDeclaredAnnotation(ReturnType.class);
        if (returnType == null) {
            throw new IllegalArgumentException("expression must define a return type");
        }
        
        // Figure out call targets, sort them based on their priorities
        List<TargetMethod> targets = new ArrayList<>();
        for (Method m : type.getDeclaredMethods()) {
            CallTarget t = m.getDeclaredAnnotation(CallTarget.class);
            if (t != null) {
                targets.add(new TargetMethod(m, t.value()));
            }
        }
        if (targets.size() == 0) {
            throw new IllegalArgumentException("at least one call target must be defined");
        }
        Collections.sort(targets);
        
        // Create call targets array
        Method[] callTargets = new Method[targets.size()];
        for (int i = 0; i < callTargets.length; i++) {
            callTargets[i] = targets.get(i).method;
        }
        
        // Create expression info
        Constant constant = type.getDeclaredAnnotation(Constant.class);
        ConstantFoldable foldable = type.getDeclaredAnnotation(ConstantFoldable.class);
        ExpressionInfo info = new ExpressionInfo(callTargets, constant != null, constant != null || foldable != null, null);
        int infoIndex = exprInfos.size();
        exprInfos.add(info);
        
        // Parse patterns and register them
        Pattern[] patternArray = patterns.value();
        for (Pattern p : patternArray) {
            for (io.github.bensku.skript.parser.pattern.Pattern pattern : patternParser.parsePatterns(p.value())) {
                patternRegistry.addSyntax(returnType.value(), pattern);
                pattern.setCompilerId(infoIndex); // Compiler will know info based on this
            }
        }
    }
    
    /**
     * Registers a constant expression for given patterns.
     * @param value Constant value.
     * @param patterns Patterns for this constant.
     */
    public void registerConstant(Object value, String... patterns) {
        ExpressionInfo info = new ExpressionInfo(null, true, true, ast -> new ConstantNode(value));
        int infoIndex = exprInfos.size();
        exprInfos.add(info);
        
        // Parse patterns and register them
        for (String p : patterns) {
            for (io.github.bensku.skript.parser.pattern.Pattern pattern : patternParser.parsePatterns(p)) {
                patternRegistry.addSyntax(value.getClass(), pattern);
                pattern.setCompilerId(infoIndex); // Compiler will know info based on this
            }
        }
    }
    
    public ExpressionParser createParser() {
        return new ExpressionParser(patternRegistry.compile());
    }
    
    public ExpressionCompiler createCompiler() {
        return new ExpressionCompiler(exprInfos.toArray(new ExpressionInfo[exprInfos.size()]));
    }
}
