package ru.otus.exampe.atm;

import ru.otus.exampe.atm.core.ATM;
import ru.otus.exampe.atm.error.InsufficientFundsException;

import java.util.Map;

public class Main {

    public static void main(String[] args) {
        ATM atm = new ATM();
        atm.deposit(100, 5);
        atm.deposit(50, 5);
        System.out.println("Баланс: "+ atm.getBalance());

        try {
            Map<Integer, Integer> withdrawn = atm.withdraw(350);
            System.out.println("Выдано " + withdrawn);
            System.out.println("Остаток " + atm);
        } catch (InsufficientFundsException e) {
            System.out.println(e.getMessage());
        }
    }

}
