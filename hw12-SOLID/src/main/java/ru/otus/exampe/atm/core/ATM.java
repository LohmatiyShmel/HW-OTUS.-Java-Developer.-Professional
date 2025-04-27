package ru.otus.exampe.atm.core;

import ru.otus.exampe.atm.cell.Cell;
import ru.otus.exampe.atm.error.InsufficientFundsException;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ATM {
    private final Map<Integer, Cell> cells = new TreeMap<>(Comparator.reverseOrder());

    public void deposit(final int denomination, final int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Количество должно быть положительным");
        }

        if (cells.containsKey(denomination)) {
            cells.get(denomination).add(count);
        } else {
            final Cell newCell = new Cell(denomination);
            newCell.add(count);
            cells.put(newCell.getDenomination(), newCell);
        }
    }

    public int getBalance() {
        return cells.values().stream()
                .mapToInt(c -> c.getDenomination() * c.getCount())
                .sum();
    }

    public Map<Integer, Integer> withdraw(final int amount) throws InsufficientFundsException {
        if (amount <= 0) {
            throw new InsufficientFundsException("Сумма должна быть положительной");
        }

        final Map<Integer, Integer> result = new TreeMap<>();
        final Map<Integer, Cell> buffer = createCellsBuffer();
        int remaining = amount;

        for (Cell cell : buffer.values()) {
            final int denomination = cell.getDenomination();
            final int maxPossible = remaining / denomination;
            final int available = cell.getCount();

            if (available == 0 || maxPossible == 0) continue;

            final int toExtract = Math.min(maxPossible, available);
            result.put(denomination, toExtract);
            remaining -= denomination * toExtract;
            cell.extract(toExtract);

            if (remaining == 0) break;
        }

        if (remaining != 0) {
            throw new InsufficientFundsException("Недостаточно средств");
        }

        updateOriginalCells(buffer);
        return result;
    }

    private TreeMap<Integer, Cell> createCellsBuffer() {
        return cells.values().stream()
                .map(c -> {
                    final Cell copy = new Cell(c.getDenomination());
                    copy.add(c.getCount());
                    return copy;
                })
                .collect(Collectors.toMap(
                        Cell::getDenomination,
                        cell -> cell,
                        (oldVal, newVal) -> oldVal,
                        () -> new TreeMap<>(Comparator.reverseOrder())
                ));
    }

    private void updateOriginalCells(final Map<Integer, Cell> buffer) {
        cells.clear();
        cells.putAll(buffer);
    }

    @Override
    public String toString() {
        return cells.entrySet().stream()
                .map(e -> e.getKey() + "x" + e.getValue().getCount())
                .collect(Collectors.joining(", "));
    }
}
