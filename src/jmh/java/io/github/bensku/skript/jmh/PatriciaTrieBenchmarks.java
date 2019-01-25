package io.github.bensku.skript.jmh;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections4.trie.PatriciaTrie;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

@BenchmarkMode(Mode.AverageTime)
public class PatriciaTrieBenchmarks {
    
    private static final int N = 10_000_000;

    @State(Scope.Benchmark)
    public static class TestData {
        
        public final PatriciaTrie<Object> trie;
        
        public TestData() {
            trie = new PatriciaTrie<>();
            
            for (int i = 0; i < N; i++) {
                trie.put(UUID.randomUUID().toString(), new Object());
            }
        }
    }
    
    @Benchmark
    public void prefixMap(TestData data, Blackhole bh) {
        Set<Map.Entry<String, Object>> set = data.trie.prefixMap("a").entrySet();
        for (Map.Entry<String, Object> entry : set) {
            bh.consume(entry.getKey());
            bh.consume(entry.getValue());
        }
    }
    
    @Benchmark
    public void prefixMap2(TestData data, Blackhole bh) {
        String str = "1fcd28c8-20e4";
        while (str.length() > 2) { // Assume most patterns start/end with more than this characters
            Set<Map.Entry<String, Object>> set = data.trie.prefixMap(str).entrySet();
            for (Map.Entry<String, Object> entry : set) {
                bh.consume(entry.getKey());
                bh.consume(entry.getValue());
            }
            str = str.substring(0, str.length() - 1);
        }
    }
}
