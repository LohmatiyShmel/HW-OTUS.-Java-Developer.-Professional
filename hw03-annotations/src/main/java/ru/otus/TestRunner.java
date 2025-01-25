package ru.otus;

import ru.otus.annotation.After;
import ru.otus.annotation.Before;
import ru.otus.annotation.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;

public class TestRunner {
    public void runTests(final String className) {
        try {
            Class<?> testClass = Class.forName(className);
            Object testInstance = testClass.getDeclaredConstructor().newInstance();

            int totalTests = 0;
            int passedTests = 0;
            int failedTests = 0;

            Method[] methods = testClass.getDeclaredMethods();
            Method[] beforeMethods = getAnnotatedMethods(methods, Before.class);
            Method[] afterMethods = getAnnotatedMethods(methods, After.class);
            Method[] testMethods = getAnnotatedMethods(methods, Test.class);

            for (Method testMethod : testMethods) {
                try {
                    totalTests++;
                    invokeMethods(testInstance, beforeMethods);
                    testMethod.invoke(testInstance);
                    System.out.println(testMethod.getName() + " прошел успешно");
                    passedTests++;
                } catch (Exception e) {
                    System.out.println(testMethod.getName() + " завершен с ошибкой: " + e.getCause());
                    failedTests++;
                } finally {
                    invokeMethods(testInstance, afterMethods);
                }
            }
            printStatistics(totalTests, passedTests, failedTests);
        } catch (Exception e) {
            System.out.println("Ошибка при запуске тестов: " + e);
        }
    }

    private Method[] getAnnotatedMethods(final Method[] methods, final Class<? extends Annotation> annotation) {
        return Arrays.stream(methods)
                .filter(m -> m.isAnnotationPresent(annotation))
                .toArray(Method[]::new);
    }

    private void invokeMethods(final Object instance, final Method[] methods) {
        for (Method method : methods) {
            try {
                method.invoke(instance);
            } catch (Exception e) {
                throw new RuntimeException("Failed to invoke method: " + method.getName(), e);
            }
        }
    }

    private void printStatistics(final int totalTests, final int passedTests, final int failedTests) {
        System.out.println("Всего тестов: " + totalTests);
        System.out.println("Прошло тестов: " + passedTests);
        System.out.println("Не пройденных тестов: " + failedTests);
    }

    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        runner.runTests("ru.otus.Tests");
    }
}
