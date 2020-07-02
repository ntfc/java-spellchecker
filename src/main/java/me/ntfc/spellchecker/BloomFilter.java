package me.ntfc.spellchecker;

import java.util.BitSet;

public class BloomFilter {

    private final int size;

    private final int numberOfHashes;

    private final BitSet bitSet;

    public BloomFilter(final int size, final int numberOfHashes) {
        this.size = size;
        this.numberOfHashes = numberOfHashes;
        this.bitSet = new BitSet(size);
        assert bitSet.size() == size;
    }

    public void add(final String word) {
        if (word == null || word.isBlank()) {
            return;
        }
        int hash = hashWord(word);

        bitSet.set(hash % size);

        assert bitSet.size() == size; // invariant
    }

    public boolean containsWord(final String word) {
        if (word == null || word.isBlank()) {
            return false;
        }

        int hash = hashWord(word);
        if (!bitSet.get(hash % size)) {
            int lowerCaseHash = hashWord(word.toLowerCase());
            return bitSet.get(lowerCaseHash % size);
        } else {
            return true;
        }

    }

    private int hashWord(final String word) {
        // taken from java.util.HashMap.hash
        int h = word.hashCode();
        return (h & 0xFFFFFFF) ^ (h >>> 16);
    }
}
