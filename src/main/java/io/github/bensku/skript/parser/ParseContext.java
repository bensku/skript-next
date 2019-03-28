package io.github.bensku.skript.parser;

/**
 * Parser context contains information necessary to create fancy error messages.
 *
 */
public class ParseContext {

    /**
     * Source associated with this parse operation.
     */
    private final Source source;
    
    /**
     * Line associated with this parse operation.
     */
    private final int line;
    
    /**
     * Start and end indices of text associated with parsing.
     */
    private final int start, end;

    public ParseContext(Source source, int line, int start, int end) {
        super();
        this.source = source;
        this.line = line;
        this.start = start;
        this.end = end;
    }

    public Source getSource() {
        return source;
    }

    public int getLine() {
        return line;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }
    
}
