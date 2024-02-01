package com.mcajben.animation_skipper;

import java.util.Random;

public class DisplayTextFactory {
    private static final Random rand = new Random();
    private static final String[] TEXT = {
            "A LITTLE LATER",
            "A LOT LATER",
            "A WHOLE LOT LATER",
            "FIVE DAYS LATER",
            "FIVE DAYS PASS",
            "FIVE HOURS LATER",
            "FIVE HOURS PASS",
            "FIVE MINUTES LATER",
            "FIVE MINUTES PASS",
            "ONE DAY LATER",
            "ONE DAY PASSES",
            "ONE HOUR LATER",
            "ONE HOUR PASSES",
            "ONE MINUTE LATER",
            "ONE MINUTE PASSES",
            "SKIPPING...",
            "SOME TIME LATER",
            "SOME TIME PASSES",
            "TEN DAYS LATER",
            "TEN DAYS PASS",
            "TEN HOURS LATER",
            "TEN HOURS PASS",
            "TEN MINUTES LATER",
            "TEN MINUTES PASS",
    };

    public static String getRandomText(String previousText) {
        if (rand.nextInt(1_000_000) == 0) {
            // Special RNG Text
            return "YOU DIED";
        }

        final int randIndex = rand.nextInt(TEXT.length - 1);
        if (TEXT[randIndex].equals(previousText)) {
            final int nextRandIndex = (randIndex + 1) % TEXT.length;
            return TEXT[nextRandIndex];
        } else {
            return TEXT[randIndex];
        }
    }
}
