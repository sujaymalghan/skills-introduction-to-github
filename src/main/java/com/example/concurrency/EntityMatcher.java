package com.example.concurrency;

import org.apache.commons.text.similarity.JaccardSimilarity;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;

import java.util.*;
import java.util.stream.Collectors;

public class EntityMatcher {
    public static double matchScore(String name1, String name2) {
        if (name1 == null) name1 = "";
        if (name2 == null) name2 = "";

        List<String> t1 = Arrays.stream(
                name1.toLowerCase().replaceAll("[^a-z0-9\\s]", "").trim().split("\\s+")
        ).collect(Collectors.toList());

        List<String> t2 = new ArrayList<>(
                Arrays.asList(
                        name2.toLowerCase().replaceAll("[^a-z0-9\\s]", "").trim().split("\\s+")
                )
        );

        if (t1.isEmpty()) return t2.isEmpty() ? 1.0 : 0.0;

        double w = 1.0 / t1.size();
        JaroWinklerSimilarity jaro = new JaroWinklerSimilarity();
        List<Double> bestSims = new ArrayList<>();

        for (String token : t1) {
            double bestSim = 0.0;
            int bestIndex = -1;
            for (int i = 0; i < t2.size(); i++) {
                double sim = jaro.apply(token, t2.get(i));
                if (sim > bestSim) {
                    bestSim = sim;
                    bestIndex = i;
                }
            }
            if (bestIndex != -1) t2.remove(bestIndex);
            bestSims.add(bestSim);
        }

        long matchCount = bestSims.stream().filter(s -> s >= 0.85).count();
        boolean flag = (matchCount >= 3);

        double score = 0.0;
        for (double s : bestSims) {
            if (s < 0.55) {
                score += flag ? -0.1 : -0.2;
            } else if (s < 0.67) {
                score += w / 3.0;
            } else if (s < 0.85) {
                score += w / 2.0;
            } else {
                score += w;
            }
        }

        if (score < 0) score = 0;
        if (score > 1) score = 1;
        return score;
    }

    public static void main(String[] args) {
        String[][] testCases = {
                {"Ben Divan telL:eq fhdfhg", "Ben Divan tel"},

        };

        JaroWinklerSimilarity j= new JaroWinklerSimilarity();
       // System.out.println(j.apply("to","tel"));
        System.out.println( matchScore(testCases[0][0],testCases[0][1]));


    }
}
