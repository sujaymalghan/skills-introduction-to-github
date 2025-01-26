package com.example.concurrency;

import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.apache.commons.text.similarity.LevenshteinDistance;

public class JaroWinklerExample {
    public static void main(String[] args) {
        // Create an instance of JaroWinklerSimilarity
        JaroWinklerSimilarity jaroWinkler = new JaroWinklerSimilarity();
        LevenshteinDistance Ld = new LevenshteinDistance();

        // Define the strings to compare
        String string1 = "Richelieu";
        String string2 = "Ricjie";

        // Calculate the similarity
        double similarity = jaroWinkler.apply(string1, string2);
    double check = Ld.apply(string1,string2);
        // Display the result
        System.out.println(check);
        System.out.println("Jaro-Winkler Similarity between \"" + string1 + "\" and \"" + string2 + "\": " + similarity);
    }
}
