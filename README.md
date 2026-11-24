# Inverted Index with Hash Tables (CME 2201 – Assignment 1)

A record-level inverted index in Java, built on a hash table implemented from scratch (no `java.util` collections). Given a word, it lists every document that contains it, with the word's frequency in each document, sorted by frequency.

The index has two levels, and both are hash tables:
- **Outer table:** word → inner table
- **Inner table:** document name (`category/file.txt`) → frequency

## Features
- `put` / `get` / `remove` / `resize(int capacity)` through a `DictionaryADT` interface
- Two hash functions: **SSF** (simple summation) and **PAF** (polynomial accumulation, z = 33, Horner's rule)
- Two collision strategies: **Linear Probing** and **Double Hashing** (`d(k) = 7 - k mod 7`, prime table sizes)
- Dynamic resizing at load factor 0.5 or 0.8 (new size = first prime ≥ 2N)
- Deletion with tombstones, so probe chains stay intact
- Stop-word removal and punctuation-based tokenization

## Class overview
| Class | Role |
|---|---|
| `DictionaryADT` | Hash table interface |
| `HashTable` | Abstract base class: probing, resize, tombstones, collision counting |
| `LinearProbingTable` / `DoubleHashingTable` | Provide the probe step (`getStep`) |
| `HashFunction`, `SsfHash`, `PafHash` | Pluggable hash functions |
| `Entry` | Key, value and erased flag |
| `InvertedIndex` | Reads documents, filters words, builds the two-level index, search/remove |
| `Main` | Menu: performance test (8 combinations) and interactive search/remove |

## Running
From the repository root:
```
javac -encoding UTF-8 -d out code/*.java
java -cp out Main
```
- **Option 1:** Runs the performance test for all 8 combinations (about 1.5 minutes).
- **Option 2:** Builds an index with the settings you choose, then lets you search and remove words.

The program reads `bbc/`, `stop_words_en.txt` and `1000.txt` from the working directory.

## Results
| Load factor | Hash | Collision | Collisions | Indexing (ms) | Avg search (ns) |
|---|---|---|---|---|---|
| 50% | SSF | LP | 361,288,367 | 22712 | 94378 |
| 50% | SSF | DH | 133,472,553 | 10539 | 38979 |
| 50% | PAF | LP | 23,907 | 1298 | 310 |
| 50% | PAF | DH | 18,236 | 1384 | 167 |
| 80% | SSF | LP | 361,291,687 | 22218 | 99624 |
| 80% | SSF | DH | 133,644,435 | 9961 | 39582 |
| 80% | PAF | LP | 140,723 | 1285 | 236 |
| 80% | PAF | DH | 70,461 | 1364 | 205 |

The choice of hash function matters most. SSF values are small and collide heavily (anagrams get the same value), while PAF spreads words over the whole table. For the full analysis, see [CME2201_Report.pdf](CME2201_Report.pdf).

## Data
The `bbc/` folder contains the BBC News dataset: 2225 articles in 5 categories (D. Greene and P. Cunningham, ICML 2006). It is included only so the project runs as is.
