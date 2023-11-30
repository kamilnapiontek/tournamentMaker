package com.example.tournamentmaker.excel.football;

import org.junit.gen5.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.example.tournamentmaker.excel.football.FootballExcelUtil.stringContainsOnlyNumbers;

class FootballExcelUtilTest {
    @Test
    void shouldReturnTrueWhenStringContainsOnlyNumbers() {
        // given
        String string = "123";
        // when
        boolean stringContainsOnlyNumbers = stringContainsOnlyNumbers(string);
        // then
        Assertions.assertTrue(stringContainsOnlyNumbers);
    }

    @Test
    void shouldReturnFalseWhenStringContainsOnlyNumbers() {
        // given
        String string = "123d";
        // when
        boolean stringContainsOnlyNumbers = stringContainsOnlyNumbers(string);
        // then
        Assertions.assertFalse(stringContainsOnlyNumbers);
    }
}