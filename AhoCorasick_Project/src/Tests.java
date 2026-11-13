import java.util.*;

public final class Tests {
    private static void check(boolean ok, String label) {
        if (!ok) throw new AssertionError(label);
        System.out.println("PASS " + label);
    }
    public static void main(String[] args) {
        List<String> patterns = Arrays.asList("he", "she", "his", "hers");
        AhoCorasick ac = new AhoCorasick(patterns);
        List<AhoCorasick.Match> found = ac.search("ahishers");
        Set<String> actual = new HashSet<>();
        for (AhoCorasick.Match m : found) actual.add(patterns.get(m.patternId) + "@" + m.start);
        check(actual.equals(new HashSet<>(Arrays.asList("his@1", "she@3", "he@4", "hers@4"))), "classic example and suffix outputs");
        check(ac.countMatches("ahishers") == 4, "count matches");
        check(KMP.countMatches("ahishers", patterns) == 4, "KMP matches AC");
        check(new AhoCorasick(Arrays.asList("a", "aa")).countMatches("aaa") == 5, "overlapping matches");
        check(new AhoCorasick(Arrays.asList("ab", "ab")).countMatches("abab") == 4, "duplicate pattern IDs");
        check(new AhoCorasick(Arrays.asList("xyz")).search("").isEmpty(), "empty text");
        check(new AhoCorasick(Arrays.asList("é", "猫")).countMatches("猫é猫") == 3, "Unicode UTF-16 characters");
        Random r = new Random(123);
        for (int trial = 0; trial < 100; trial++) {
            StringBuilder s = new StringBuilder();
            for (int j = 0; j < 100; j++) s.append((char) ('a' + r.nextInt(3)));
            List<String> ps = new ArrayList<>();
            for (int k = 0; k < 20; k++) {
                StringBuilder p = new StringBuilder();
                for (int j = 0; j < 1 + r.nextInt(6); j++) p.append((char) ('a' + r.nextInt(3)));
                ps.add(p.toString());
            }
            check(new AhoCorasick(ps).countMatches(s.toString()) == KMP.countMatches(s.toString(), ps), "random trial " + trial);
        }
        System.out.println("All tests passed.");
    }
}
