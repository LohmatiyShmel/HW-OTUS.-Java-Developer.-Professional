package ru.otus.exampe.atm.cell;

public class Cell {
    private final int denomination;
    private int count;

    public Cell(final int denomination) {
        if (denomination <= 0) {
            throw new IllegalArgumentException("Номинал должен быть положительным");
        }
        this.denomination = denomination;
        this.count = 0;
    }

    public int getDenomination() {
        return denomination;
    }

    public int getCount() {
        return count;
    }

    public void add(final int count) {
        if (count<= 0) {
            throw new IllegalArgumentException("Количество должно быть положительным");
        }
        this.count += count;
    }

    public void extract(final int count) {
        if (count <= 0 || this.count < count) {
            throw new IllegalArgumentException("Невозможно извлечь указанное количество");
        }
        this.count -= count;
    }
}
