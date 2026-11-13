import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class Benchmark {
    private static volatile long blackhole;
    private static String randomString(Random r, int length) {
        StringBuilder b = new StringBuilder(length);
        for (int i = 0; i < length; i++) b.append((char) ('a' + r.nextInt(26)));
        return b.toString();
    }
    public static void main(String[] args) throws IOException {
        Path output = Paths.get("results", "benchmark.csv");
        Files.createDirectories(output.getParent());
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(output))) {
            out.println("patterns,text_length,run,ac_build_ms,ac_search_ms,ac_total_ms,kmp_total_ms,matches");
            for (int count : new int[]{10, 100, 1000}) {
                for (int size : new int[]{1000, 10000, 100000}) {
                    Random r = new Random(42L + count * 1000000L + size);
                    List<String> patterns = new ArrayList<>();
                    for (int p = 0; p < count; p++) patterns.add(randomString(r, 3 + r.nextInt(6)));
                    String text = randomString(r, size);
                    for (int w = 0; w < 3; w++) {
                        AhoCorasick warm = new AhoCorasick(patterns);
                        blackhole = warm.countMatches(text);
                        blackhole = KMP.countMatches(text, patterns);
                    }
                    double sumAc = 0, sumKmp = 0;
                    for (int run = 1; run <= 5; run++) {
                        long t0 = System.nanoTime();
                        AhoCorasick ac = new AhoCorasick(patterns);
                        long t1 = System.nanoTime();
                        long acMatches = ac.countMatches(text);
                        long t2 = System.nanoTime();
                        long kmpMatches = KMP.countMatches(text, patterns);
                        long t3 = System.nanoTime();
                        if (acMatches != kmpMatches) throw new AssertionError("Mismatch for " + count + "," + size);
                        blackhole = acMatches;
                        double build = (t1 - t0) / 1e6;
                        double search = (t2 - t1) / 1e6;
                        double total = (t2 - t0) / 1e6;
                        double kmp = (t3 - t2) / 1e6;
                        sumAc += total;
                        sumKmp += kmp;
                        out.printf(Locale.US, "%d,%d,%d,%.6f,%.6f,%.6f,%.6f,%d%n", count, size, run, build, search, total, kmp, acMatches);
                    }
                    System.out.printf(Locale.US, "patterns=%4d text=%6d AC=%.3f ms KMP=%.3f ms%n", count, size, sumAc / 5, sumKmp / 5);
                }
            }
        }
        System.out.println("Saved " + output);
    }
}
