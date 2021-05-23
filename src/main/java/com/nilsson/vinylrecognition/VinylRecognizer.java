package com.nilsson.vinylrecognition;

import com.nilsson.vinylrecognition.catno.CatalogueNumberExtractor;
import com.nilsson.vinylrecognition.file.FileMover;
import com.nilsson.vinylrecognition.ocr.OCRFacade;
import com.nilsson.vinylrecognition.preprocessing.ImagePreProcessorFacade;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

public class VinylRecognizer {
    //    private static final Path TARGET_DIR = Paths.get("src", "main", "resources", "images");
    private static final Path SOURCE_DIR = Paths.get("/home/andreas/Bilder/lp");
    private static final Path TARGET_DIR = Paths.get("/home/andreas/Bilder/interesting_lp");
    private static final Logger LOG = getLogger(lookup().lookupClass());
    private final ImagePreProcessorFacade imagePreProcessor;
    private final OCRFacade ocrFacade;

    public VinylRecognizer(ImagePreProcessorFacade imagePreProcessor, OCRFacade ocrFacade) {
        this.imagePreProcessor = imagePreProcessor;
        this.ocrFacade = ocrFacade;
    }

    public void run() throws IOException {
        FileUtils.cleanDirectory(TARGET_DIR.toFile());
        FileMover.moveEveryNthFiles(SOURCE_DIR, TARGET_DIR, 5);

        imagePreProcessor.preProcess(getSortedJpgFiles());

        File[] sortedTiffFiles = getSortedTiffFiles();
        LOG.debug(Arrays.toString(sortedTiffFiles));
        List<String> catalogueNumbers = Arrays.stream(sortedTiffFiles)
                .map(ocrFacade::extractTextFromImage)
                .map(CatalogueNumberExtractor::extractCatalogueNumber)
                .collect(Collectors.toList());
        LOG.debug("catalogueNumbers = {}", catalogueNumbers);

        Set<String> correctCatalogueNumbers = correctCatalogueNumbers();
        List<String> correctlyMatched = catalogueNumbers.stream()
                .filter(correctCatalogueNumbers::contains)
                .collect(Collectors.toList());
        LOG.debug("Correctly matched: {}", correctlyMatched);
        long matchedCount = correctlyMatched.size();
        LOG.info("Correctly matched = {}/{} ({}%)", matchedCount, correctCatalogueNumbers.size(), matchedCount * 100 / (double) correctCatalogueNumbers.size());
        List<String> falsePositives = catalogueNumbers.stream()
                .filter(s -> !correctCatalogueNumbers.contains(s))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        LOG.debug("False positives {}", falsePositives);

        long notMatched = correctCatalogueNumbers.size() - matchedCount;
        long falsePositivesCount = falsePositives.size();
        LOG.info("False positives (lower is better): {}/{} ({}%)", falsePositivesCount, notMatched, falsePositivesCount * 100 / ((double) notMatched));

    }

    private static File[] getSortedTiffFiles() {
        return getSortedFiles(".tiff");
    }

    private static File[] getSortedJpgFiles() {
        return getSortedFiles(".jpg");
    }

    private static File[] getSortedFiles(String s) {
        File[] files = TARGET_DIR.toFile().listFiles((d, name) -> name.endsWith(s));
        assert files != null;
        Arrays.sort(files, Comparator.comparing(File::lastModified));
        return files;
    }


    public static Set<String> correctCatalogueNumbers() {
        return new HashSet<>(Arrays.asList("KULP-3300", "7C 034-34207", "POLL 117", "SSL 10247", "LPRO 51", "HLP-10521-A"
                , "7C 138-35747", "2600831", "6316 090", "PL 40164", "KLP 8", "MLPH 1622", "BBRLP 108", "468248 1", "HLP 10.533"
                , "MLP 15.555", "LBLP 008", "2379.007", "ARLP 101", "ABLP-501", "80 443 XU", "SLP-3124", "4E 048-35144", "PMES 572"));
    }
}
