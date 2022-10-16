package com.example.recipes.presentation.utils;

public enum TestData {
    DATA_1,
    DATA_2,
    DATA_4,
    DATA_3
}

// in code use:
/*
        val test = TestData.DATA_1
        when(test) {
            TestData.DATA_1 -> {...}
            TestData.DATA_2-> {...}
            TestData.DATA_3 -> {...}
            TestData.DATA_4 -> {...}
        }.exclusive
        val <T> T.exclusive: T
            get() = this
 */


