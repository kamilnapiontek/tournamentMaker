package com.example.tournamentMaker.randomResultGenerator;

import java.util.Random;

class Util {
    private final static Random random = new Random();
    public static int generateRandomNumber(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }
}
