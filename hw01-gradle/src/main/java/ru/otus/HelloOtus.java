package ru.otus;

import com.google.common.base.Preconditions;

public class HelloOtus {

    public static void main(String[] args) {
        doSomething();
    }

    public static void doSomething() {
        String name = "Жора Бора";
        String checkedName = Preconditions.checkNotNull(name, "Name cannot be null");
        System.out.println(checkedName);
    }

}
