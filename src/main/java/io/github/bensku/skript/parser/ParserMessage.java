package io.github.bensku.skript.parser;

/**
 * A message from the parser.
 *
 */
public class ParserMessage {
    
    /**
     * Parse context associated with this message.
     */
    private final ParseContext context;
    
    public enum Type {
        
        /**
         * Parser could not finish due to an error.
         */
        ERROR,
        
        /**
         * Parser finished successfully, but noticed something anomalous
         */
        WARNING
    }
    
    /**
     * Type of the message.
     */
    private final Type type;
    
    /**
     * Message produced by the parser.
     */
    private final String message;

    public ParserMessage(ParseContext context, Type type, String message) {
        super();
        this.context = context;
        this.type = type;
        this.message = message;
    }

    public ParseContext getContext() {
        return context;
    }

    public Type getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
    
}
