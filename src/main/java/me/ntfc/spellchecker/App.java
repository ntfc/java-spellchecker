package me.ntfc.spellchecker;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

public class App {

    private static final int BITMAP_SIZE = 2_849_827;

    private static final int NUMBER_OF_HASHES = 6;

    public static void main(String[] args) throws IOException {
        Path dictPath = Paths.get("/home/nuno/Projects/java-spellchecker/wordlist.txt");
        Path text = Paths.get("/home/nuno/Projects/java-spellchecker/example-text.txt");

        BloomFilter b2 = new BloomFilter(BITMAP_SIZE, NUMBER_OF_HASHES);
        Dictionary dictionary = new Dictionary(dictPath, b2);

        Set<String> spellcheck = dictionary.spellcheck(text);
        System.out.println(spellcheck);

    }

}
