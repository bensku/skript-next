package io.github.bensku.skript.annotate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies one of patterns for this expression.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(Patterns.class)
public @interface Pattern {
    
    /**
     * A pattern.
     * @return Pattern.
     */
    String value();
}
