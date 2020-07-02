package me.ntfc.spellchecker;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;

public class Dictionary {

    private final BloomFilter bloomFilter;

    public Dictionary(final Path dictionaryPath, final BloomFilter bloomFilter) throws IOException {
        Objects.requireNonNull(dictionaryPath);
        Objects.requireNonNull(bloomFilter);

        this.bloomFilter = bloomFilter;
        readDictionaryToBloomFilter(dictionaryPath);

    }

    private void readDictionaryToBloomFilter(final Path dictionaryPath) throws IOException {
        // TODO: detect file encoding
        BufferedReader reader = Files.newBufferedReader(dictionaryPath, StandardCharsets.ISO_8859_1);
        String line;
        while ((line = reader.readLine()) != null) {
            this.bloomFilter.add(line);
        }
    }


    public Set<String> spellcheck(final Path textFile) throws IOException {
        Objects.requireNonNull(textFile);

        Set<String> failures = new HashSet<>();
        Files.lines(textFile).forEach(line -> {
            Scanner lineScanner = new Scanner(line);
            lineScanner.useDelimiter("[ ,.]+");
            while (lineScanner.hasNext()) {
                String word = lineScanner.next();
                if (!bloomFilter.containsWord(word)) {
                    failures.add(word);
                }
            }
        });
        return failures;
    }
}
