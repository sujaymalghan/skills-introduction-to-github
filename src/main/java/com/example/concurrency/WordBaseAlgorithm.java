package com.example.concurrency;


import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class WordBaseAlgorithm {
    private static final double JARO_WEIGHTAGE = 0.6;
private static final double DEFAULT_THRESHOLD=0.65;
    private static final double LEGAL_WORDS_SCORE = 0.5;

    private static final double INDEX_BASED_SCORE_MULTIPLIER = 0.8;
    private static final List<String> LEGAL_STRUCTURE_NAMES = Arrays.asList(
            "limited", "ltd", "llc", "inc", "capital", "trust", "fund", "group",
            "management", "co", "company", "bank", "sa", "lp", "entity", "holdings",
            "investment", "bin", "investments", "of", "online", "deposits", "corporation",
            "partners", "global", "securities", "jr", "al", "holding", "financial",
            "trading", "china", "asset", "services", "family", "development", "energy",
            "pte", "technology", "foundation", "bo", "undisclosed", "branch", "li",
            "national", "york", "hong", "kong", "opportunity", "opportunities",
            "joint", "neto", "filho", "sobrinho", "junior", "y", "de", "la", "dos", "de la"
    );

    private static double fetchSimilarity(String left, String right) {
        left = left.replaceAll("[.]+", "").replaceAll("[-,|&()]+"," ").toLowerCase().trim();
        right = right.replaceAll("[.]+", "").replaceAll("[-,|&()]+"," ").toLowerCase().trim();

        List<String> leftNameWords = Arrays.stream(left.split("\\s+")).collect(Collectors.toList());
        List<String> rightNameWords = Arrays.stream(right.split("\\s+")).collect(Collectors.toList());

        List<String> commonLegalStructureNames = leftNameWords.stream()
                .filter(LEGAL_STRUCTURE_NAMES::contains)
                .filter(rightNameWords::contains)
                .toList();

        leftNameWords.removeAll(LEGAL_STRUCTURE_NAMES);
        rightNameWords.removeAll(LEGAL_STRUCTURE_NAMES);

        double namesSimilarityScore = 0.0;
        JaroWinklerSimilarity jaroWinklerSimilarity = new JaroWinklerSimilarity();
        LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
        Set<Integer> usedWordsOfRightName = new HashSet<>();

        for (int i = 0; i < leftNameWords.size(); i++) {
            double wordSimilarityScore = 0.0;
            int similarRightWordIndex = -1;

            for (int j = 0; j < rightNameWords.size(); j++) {
                String shortWord = leftNameWords.get(i);
                String longWord = rightNameWords.get(j);

                if (shortWord.length() > longWord.length()) {
                    String temp = shortWord;
                    shortWord = longWord;
                    longWord = temp;
                }

                double wordCombinationSimilarityScore = 0.0;
                if (shortWord.equals(longWord)) {
                    wordCombinationSimilarityScore = 2.0;
                } else if (longWord.startsWith(shortWord)) {
                    wordCombinationSimilarityScore = Math.min(0.8 + shortWord.length() * 0.15, 1.9);
                } else {
                    int lvDist = levenshteinDistance.apply(shortWord, longWord);
                    double lvSim = 1.0 - 1.0 * lvDist / Math.max(shortWord.length(), longWord.length());
                    wordCombinationSimilarityScore = JARO_WEIGHTAGE *
                            jaroWinklerSimilarity.apply(shortWord, longWord) +
                            (1.0 - JARO_WEIGHTAGE) * lvSim;

                    wordCombinationSimilarityScore=wordCombinationSimilarityScore>DEFAULT_THRESHOLD?wordCombinationSimilarityScore:0.0;
                }

                if (wordCombinationSimilarityScore > wordSimilarityScore) {
                    wordSimilarityScore = wordCombinationSimilarityScore;
                    similarRightWordIndex = j;
                }
            }

            if (usedWordsOfRightName.contains(similarRightWordIndex)) wordSimilarityScore /= 2;
            if (wordSimilarityScore > 1.2) usedWordsOfRightName.add(similarRightWordIndex);

            namesSimilarityScore += wordSimilarityScore;
        }
        namesSimilarityScore += 0.5 * commonLegalStructureNames.size();

        return namesSimilarityScore;
    }

    public static void main(String[] args) {

        System.out.println( fetchSimilarity("SHING-MING HA","STEPHEN KWAN CHI SHING")
);
    }

}