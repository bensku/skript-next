package io.github.bensku.skript.annotate;

import java.util.HashMap;
import java.util.Map;

/**
 * Types are registered here.
 *
 */
public class TypeRegistry {
    
    /**
     * Types that have been registered.
     */
    final Map<String, Class<?>> types;
    
    public TypeRegistry() {
        this.types = new HashMap<>();
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

}
