import java.util.*;

/** Independent KMP scan for each pattern; counts overlapping occurrences. */
public final class KMP {
    private KMP() {}
    private static int[] prefix(String p) {
        int[] pi = new int[p.length()];
        for (int i = 1, j = 0; i < p.length(); i++) {
            while (j > 0 && p.charAt(i) != p.charAt(j)) j = pi[j - 1];
            if (p.charAt(i) == p.charAt(j)) j++;
            pi[i] = j;
        }
        return pi;
    }
    public static long countMatches(String text, List<String> patterns) {
        long total = 0;
        for (String p : patterns) {
            if (p.isEmpty()) throw new IllegalArgumentException("Empty pattern");
            int[] pi = prefix(p);
            for (int i = 0, j = 0; i < text.length(); i++) {
                while (j > 0 && text.charAt(i) != p.charAt(j)) j = pi[j - 1];
                if (text.charAt(i) == p.charAt(j)) j++;
                if (j == p.length()) {
                    total++;
                    j = pi[j - 1];
                }
            }
        }
        return total;
    }
}
