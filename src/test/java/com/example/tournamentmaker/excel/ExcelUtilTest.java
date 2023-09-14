package com.example.tournamentmaker.excel;

import org.junit.gen5.api.Assertions;
import org.junit.jupiter.api.Test;

class ExcelUtilTest {

    @Test
    void shouldReturnTrueWhenStringContainsOnlyNumbers() {
        // given
        String string = "123";
        // when
        boolean stringContainsOnlyNumbers = ExcelUtil.stringContainsOnlyNumbers(string);
        // then
        Assertions.assertTrue(stringContainsOnlyNumbers);
    }
    @Test
    void shouldReturnFalseWhenStringContainsOnlyNumbers() {
        // given
        String string = "123d";
        // when
        boolean stringContainsOnlyNumbers = ExcelUtil.stringContainsOnlyNumbers(string);
        // then
        Assertions.assertFalse(stringContainsOnlyNumbers);
    }
}