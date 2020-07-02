package me.ntfc.spellchecker;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.BitSet;
import java.util.Scanner;

public class App {


    private static final int N = 350_000;

    private static final int BITMAP_SIZE = 2_849_827;

    private static final int NUMBER_OF_HASHES = 6;

    public static void main(String[] args) throws IOException {
        Path dictPath = Paths.get("/home/nuno/Projects/java-spellchecker/wordlist.txt");

        BufferedReader dictReader = Files.newBufferedReader(dictPath, StandardCharsets.ISO_8859_1);

        BitSet bloomFilter = new BitSet(BITMAP_SIZE);
        System.out.println("Cardinality: " + bloomFilter.cardinality());

        String line;
        while ((line = dictReader.readLine()) != null) {
            int hash = hashWord(line);
            bloomFilter.set(hash);
        }
        System.out.println("Cardinality: " + bloomFilter.cardinality());


        Path text = Paths.get("/home/nuno/Projects/java-spellchecker/example-text.txt");

        Files.lines(text).forEach(it -> {
            Scanner lineScanner = new Scanner(it);
            lineScanner.useDelimiter("[ ,.]+");
            while (lineScanner.hasNext()) {
                String word = lineScanner.next();
                if (!bloomFilter.get(hashWord(word)) && !bloomFilter.get(hashWord(word.toLowerCase()))) {
                    System.out.println("Fix: " + word);
                }

            }
        });
    }

    private static int hashWord(String word) {
        // taken from java.util.HashMap.hash
        int h;
        int hash = (h = word.hashCode() & 0xFFFFFFF) ^ (h >>> 16);
        return hash % BITMAP_SIZE;
    }

}
