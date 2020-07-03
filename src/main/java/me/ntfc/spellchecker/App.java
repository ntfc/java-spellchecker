package me.ntfc.spellchecker;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.util.Set;
import java.util.concurrent.Callable;

@Command(name = "java-spellchecker", mixinStandardHelpOptions = true)
public class App implements Callable<Integer> {

    @Option(names = {"-m", "--size"}, paramLabel = "NUM", defaultValue = "2500000", description = "the bloom filter size (i.e. number of bits)")
    int bitMapSize;

    @Option(names = {"-k", "--hashes"}, paramLabel = "NUM", defaultValue = "6", description = "number of hashes")
    int numberOfHashes;

    @Option(names = {"-d", "--dict"}, paramLabel = "FILE", required = true, description = "the dictionary file")
    File dictionaryFile;

    @Option(names = {"-f", "--file"}, paramLabel = "FILE", required = true, description = "the text to spellcheck")
    File textFile;

    @Option(names = {"--verbose"})
    boolean isVerbose;

    public static void main(String[] args) {
        System.exit(new CommandLine(new App()).execute(args));
    }

    @Override
    public Integer call() throws Exception {
        BloomFilter bloomFilter = new BloomFilter(bitMapSize, numberOfHashes);
        Dictionary dictionary = new Dictionary(dictionaryFile.toPath(), bloomFilter);

        Set<String> spellcheck = dictionary.spellcheck(textFile.toPath());

        if (isVerbose) {
            System.out.println(bloomFilter.toString());
        }
        System.out.println(spellcheck);

        return spellcheck.size();
    }
}
