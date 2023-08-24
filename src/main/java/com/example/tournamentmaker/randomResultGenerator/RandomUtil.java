package com.example.tournamentmaker.randomResultGenerator;

import com.example.tournamentmaker.team.player.FootballPosition;

import java.util.Random;

class RandomUtil {
    private final static Random random = new Random();
    private static final int ZERO_PERCENT = 0;
    private static final int ONE_HUNDRED_PERCENT = 100;

    public static int generateRandomNumber(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    static FootballPosition randomizePositionOfGoalScorer() {
        int randomNumber = generateRandomNumber(ZERO_PERCENT, ONE_HUNDRED_PERCENT);
        if (randomNumber < 40) return FootballPosition.FORWARD;
        if (randomNumber < 80) return FootballPosition.MIDFIELDER;
        if (randomNumber < 100) return FootballPosition.DEFENDER;
        return FootballPosition.GOALKEEPER;
    }

    static int getRandomGoalsNumber() {
        int randomNumber = generateRandomNumber(ZERO_PERCENT, ONE_HUNDRED_PERCENT);
        if (randomNumber < 20) return 0;
        if (randomNumber < 42) return 1;
        if (randomNumber < 64) return 2;
        if (randomNumber < 80) return 3;
        if (randomNumber < 90) return 4;
        if (randomNumber < 94) return 5;
        if (randomNumber < 97) return 6;
        if (randomNumber < 98) return 7;
        if (randomNumber < 99) return 8;
        if (randomNumber < 100) return 9;
        return 10;
    }

    static int getRandomRedCardCount() {
        int randomNumber = generateRandomNumber(ZERO_PERCENT, ONE_HUNDRED_PERCENT);
        if (randomNumber < 95) return 0;
        if (randomNumber < 99) return 1;
        return 2;
    }
}
