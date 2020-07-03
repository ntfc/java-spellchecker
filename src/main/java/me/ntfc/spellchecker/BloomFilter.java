package me.ntfc.spellchecker;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.StringJoiner;

public class BloomFilter {

    private final int size;

    private final int numberOfHashes;

    private final BitSet bitSet;

    private static final HashFunction murmur3_128 = Hashing.murmur3_128();

    public BloomFilter(final int size, final int numberOfHashes) {
        this.size = size;
        this.numberOfHashes = numberOfHashes;
        this.bitSet = new BitSet(size);
        assert bitSet.size() == size; // invariant
    }

    public void add(final String word) {
        if (word == null || word.isBlank()) {
            return;
        }
        int[] wordHashes = hashWord(word);

        for (int hash : wordHashes) {
            bitSet.set(hash);
        }

        assert bitSet.size() == size; // invariant
    }

    public boolean containsWord(final String word) {
        if (word == null || word.isBlank()) {
            return false;
        }

        int[] wordHashes = hashWord(word);

        for (int hash : wordHashes) {
            if (!bitSet.get(hash)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Create {@code numberOfHashes} hashes for word {@code word}.
     *
     * <p>First this will create a 128 bit hash of {@code word} (i.e. 16 bytes). This 16 bytes hash is used to create
     * two {@code long} numbers (each with 8 bytes).
     * They are then used to derive {@code numberOfHashes} {@code int} numbers (modulo {@code size}) that will be used
     * for bloom filter insertion/comparison.
     *
     * @return the {@code numberOfHashes} integers to be use as hashes of {@code word}
     */
    private int[] hashWord(final String word) {
        int[] hashes = new int[numberOfHashes];

        HashCode wordHashCode = murmur3_128.hashBytes(word.getBytes());
        byte[] wordHashCodeAsBytes = wordHashCode.asBytes();

        assert wordHashCode.bits() == 128;
        assert wordHashCodeAsBytes.length == 16;

        long long1 = ByteBuffer.wrap(wordHashCodeAsBytes, 0, Long.BYTES /* 8 */).getLong();
        long long2 = ByteBuffer.wrap(wordHashCodeAsBytes, 8, Long.BYTES /* 8 */).getLong();

        // create "k" hashes
        long hash = long1;
        for (int i = 0; i < numberOfHashes; i++) {
            hash += numberOfHashes * long2;
            int hashMod = Math.abs((int) (hash % size));
            hashes[i] = hashMod;
        }

        return hashes;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", BloomFilter.class.getSimpleName() + "[", "]")
                .add("capacity=" + size)
                .add("elements=" + bitSet.cardinality())
                .add("bitSet=" + bitSet)
                .toString();
    }
}
