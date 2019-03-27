package io.github.bensku.skript;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Represents source code of a script.
 *
 */
public class Source {
    
    public static Source fromFile(Path file) throws IOException {
        return new Source(file.getFileName().toString(), file, Files.readAllLines(file));
    }
    
    public static Source fromString(String name, List<String> lines) {
        return new Source(name, null, lines);
    }
    
    /**
     * Name of the script source.
     */
    private final String name;
    
    /**
     * Source file, if available.
     */
    private final Path file;
    
    /**
     * Code lines.
     */
    private final List<String> lines;

    private Source(String name, Path file, List<String> lines) {
        this.name = name;
        this.file = file;
        this.lines = lines;
    }

    public String getName() {
        return name;
    }

    public Path getFile() {
        return file;
    }

    public List<String> getLines() {
        return lines;
    }
    
    
}
