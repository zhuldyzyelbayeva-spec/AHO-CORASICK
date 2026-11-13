# Aho–Corasick vs KMP — Java Project

## About the project

This project implements the Aho–Corasick algorithm in Java to find multiple patterns in a text. It also implements KMP to compare their execution times. Both algorithms find overlapping matches.

## Project files

- `src/AhoCorasick.java` — trie, failure links, and pattern matching.
- `src/KMP.java` — KMP algorithm for searching each pattern separately.
- `src/Tests.java` — correctness and randomized tests.
- `src/Benchmark.java` — performance tests using `System.nanoTime()`.
- `results/benchmark.csv` — results from all 45 timed runs.
- `results/averages.md` — average execution times.

## How to run

Java 11 or newer is required. Open the project in IntelliJ IDEA and run `Tests.java` and `Benchmark.java`.

You can also use the terminal from the project folder:

```bash
mkdir -p out
javac -d out src/*.java
java -cp out Tests
java -cp out Benchmark
```

The benchmark saves its results in `results/benchmark.csv`.

## How Aho–Corasick works

1. **Trie:** All patterns are inserted into a tree. Patterns with the same prefix share nodes.
2. **Failure links:** These links are built using breadth-first search (BFS). When there is no matching edge, the algorithm follows a failure link to continue from the longest possible suffix that is also a trie prefix.
3. **Output lists:** Each node stores the IDs of patterns ending there. It also inherits matches from its failure node, so shorter and overlapping patterns are not missed.
4. **Search:** The algorithm reads the text from left to right. At each position, it records all patterns found at the current node. The `search()` method returns match positions, while `countMatches()` counts them without creating a list of results.

## Testing method

I tested 10, 100, and 1,000 patterns with text lengths of 1,000, 10,000, and 100,000 characters. This gives 9 test configurations. The patterns and text were generated from lowercase letters using a fixed random seed. Pattern lengths were between 3 and 8 characters.

Each configuration had 3 warm-up runs and 5 timed runs. I measured execution time with `System.nanoTime()` and calculated the average. Aho–Corasick time includes building the trie and searching. KMP time includes preparing and searching each pattern. Both algorithms were checked to make sure they found the same number of matches.

## Experimental results

Average times from the recorded runs (milliseconds):

| Patterns | Text length | AC build | AC search | AC total | KMP total |
|---:|---:|---:|---:|---:|---:|
| 10 | 1,000 | 0.226 | 0.244 | 0.470 | 0.880 |
| 10 | 10,000 | 0.086 | 0.619 | 0.705 | 1.830 |
| 10 | 100,000 | 0.056 | 1.465 | 1.521 | 2.801 |
| 100 | 1,000 | 0.582 | 0.012 | 0.594 | 0.297 |
| 100 | 10,000 | 0.558 | 0.143 | 0.701 | 3.081 |
| 100 | 100,000 | 0.601 | 1.477 | 2.078 | 32.478 |
| 1,000 | 1,000 | 4.099 | 0.021 | 4.120 | 2.852 |
| 1,000 | 10,000 | 0.756 | 0.223 | 0.979 | 30.294 |
| 1,000 | 100,000 | 3.578 | 2.144 | 5.723 | 302.210 |

The results show that Aho–Corasick is generally faster when the number of patterns and the text length increase. For short texts, building the trie can take more time than searching, so KMP can sometimes be faster. Execution times may be different on other computers.

## Time and space complexity

Let:

- `S` = total length of all patterns
- `N` = length of the text
- `P` = number of patterns
- `L` = length of the longest pattern
- `Z` = number of matches
- `D` = total number of inherited output entries stored in trie nodes

**Aho–Corasick:** Building the trie takes O(S) expected time. For this implementation, building failure links takes at most O(S × L) time, plus O(D) for copying output lists. Searching takes O(N + Z) time because failure steps are amortized over the scan and reporting matches takes time. The trie and its output lists use O(S + D) space; returning all matches requires another O(Z) space. The count-only method avoids storing those matches.

**KMP:** Searching separately for all patterns takes O(P × N + S) time. When patterns are processed one by one, its extra working memory is O(L), excluding the input patterns and any stored results.

## Comparison and conclusion

Aho–Corasick searches for many patterns in one pass through the text. It is useful for large texts and large sets of patterns, but building and storing the trie requires additional memory. KMP is simpler and uses less extra memory for one pattern, but it must scan the text again for every additional pattern.

Overall, the experiments show that Aho–Corasick is a better choice for searching many patterns at once, especially as the text and pattern set become larger.
