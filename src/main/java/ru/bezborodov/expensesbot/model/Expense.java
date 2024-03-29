package ru.bezborodov.expensesbot.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity(name = "Expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Enumerated(EnumType.STRING)
    private ExpenseCategory category;

    private double price;

    private LocalDate date;

    private int messageId;

    @Override
    public String toString() {
        return "Expense{" +
                "id=" + id +
                ", user=" + user.getId() +
                ", category=" + category +
                ", price=" + price +
                '}';
    }
}
