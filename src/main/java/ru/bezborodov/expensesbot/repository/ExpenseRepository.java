package ru.bezborodov.expensesbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bezborodov.expensesbot.model.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

}
