package ru.otus;

import ru.otus.annotation.After;
import ru.otus.annotation.Before;
import ru.otus.annotation.Test;

public class Tests {

    @Before
    void setUp() {
        System.out.println("Начинаем...");
    }

    @Test
    void testFailure() {
        throw new RuntimeException("Тест не прошел");
    }

    @Test
    void testSuccessful() {
        System.out.println("Тест прошел");
    }

    @After
    void tearDown() {
        System.out.println("...Заканчиваем");
    }

}
