import java.util.*;

/** Multi-pattern matching with a trie, failure links, and inherited outputs. */
public final class AhoCorasick {
    private static final class Node {
        final Map<Character, Node> next = new HashMap<>();
        Node fail;
        final List<Integer> outputs = new ArrayList<>();
    }

    public static final class Match {
        public final int patternId;
        public final int start;
        public final int endExclusive;
        Match(int patternId, int start, int endExclusive) {
            this.patternId = patternId;
            this.start = start;
            this.endExclusive = endExclusive;
        }
        @Override public String toString() {
            return "pattern=" + patternId + " at [" + start + "," + endExclusive + ")";
        }
    }

    private final Node root = new Node();
    private final int[] lengths;

    public AhoCorasick(List<String> patterns) {
        if (patterns == null || patterns.isEmpty()) throw new IllegalArgumentException("Patterns required");
        lengths = new int[patterns.size()];
        for (int id = 0; id < patterns.size(); id++) {
            String pattern = Objects.requireNonNull(patterns.get(id));
            if (pattern.isEmpty()) throw new IllegalArgumentException("Empty patterns not supported");
            lengths[id] = pattern.length();
            Node current = root;
            for (int j = 0; j < pattern.length(); j++) {
                current = current.next.computeIfAbsent(pattern.charAt(j), k -> new Node());
            }
            current.outputs.add(id);
        }
        buildFailureLinks();
    }

    private void buildFailureLinks() {
        root.fail = root;
        ArrayDeque<Node> queue = new ArrayDeque<>();
        for (Node child : root.next.values()) {
            child.fail = root;
            queue.add(child);
        }
        while (!queue.isEmpty()) {
            Node parent = queue.remove();
            for (Map.Entry<Character, Node> edge : parent.next.entrySet()) {
                char c = edge.getKey();
                Node child = edge.getValue();
                Node fallback = parent.fail;
                while (fallback != root && !fallback.next.containsKey(c)) fallback = fallback.fail;
                Node candidate = fallback.next.get(c);
                child.fail = candidate == null ? root : candidate;
                // Inherit suffix matches, including multiple patterns ending here.
                child.outputs.addAll(child.fail.outputs);
                queue.add(child);
            }
        }
    }

    public List<Match> search(String text) {
        Objects.requireNonNull(text);
        List<Match> matches = new ArrayList<>();
        Node state = root;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            while (state != root && !state.next.containsKey(c)) state = state.fail;
            state = state.next.getOrDefault(c, root);
            for (int id : state.outputs) matches.add(new Match(id, i + 1 - lengths[id], i + 1));
        }
        return matches;
    }

    /** Counts matches without allocating Match objects; used in benchmarks. */
    public long countMatches(String text) {
        Objects.requireNonNull(text);
        long count = 0;
        Node state = root;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            while (state != root && !state.next.containsKey(c)) state = state.fail;
            state = state.next.getOrDefault(c, root);
            count += state.outputs.size();
        }
        return count;
    }
}
