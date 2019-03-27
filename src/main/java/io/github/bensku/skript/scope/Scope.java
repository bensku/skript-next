package io.github.bensku.skript.scope;

import java.util.Map;

import io.github.bensku.skript.parser.ExpressionParser;

public class Scope {
    
    /**
     * Parses statements.
     */
    private final ExpressionParser statementParser;
    
    /**
     * Parses scopes.
     */
    private final ExpressionParser scopeParser;
    
    /**
     * Scopes that may exist under this scope.
     */
    private final Map<Integer, Scope> childScopes;
    
    public Scope(ExpressionParser statementParser, ExpressionParser scopeParser, Map<Integer, Scope> childScopes) {
        this.statementParser = statementParser;
        this.scopeParser = scopeParser;
        this.childScopes = childScopes;
    }
}
