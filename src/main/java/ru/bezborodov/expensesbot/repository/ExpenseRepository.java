package ru.bezborodov.expensesbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.bezborodov.expensesbot.model.Expense;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    @Query("SELECT e FROM Expenses e WHERE e.user.id = :userId and e.messageId = :messageId")
    Optional<Expense> findByUserIdAndMessageId(long userId, int messageId);

    @Query("SELECT e.category, SUM(e.price) " +
            "FROM Expenses e " +
            "WHERE e.user.id = :userId AND YEAR(e.date) = :year AND MONTH(e.date) = :month " +
            "GROUP BY e.user.id, e.category " +
            "ORDER BY e.category DESC")
    List<Object[]> getMonthReportByCategories(long userId, int year, int month);

    @Query("SELECT SUM(e.price) " +
            "FROM Expenses e " +
            "WHERE e.user.id = :userId AND YEAR(e.date) = :year AND MONTH(e.date) = :month " +
            "GROUP BY e.user.id " +
            "ORDER BY SUM(e.price) DESC")
    List<Object[]> getTotalMonthReport(long userId, int year, int month);

    @Query("SELECT MONTH(e.date) as month, SUM(e.price) as sum " +
            "FROM Expenses e " +
            "WHERE e.user.id = :userId AND YEAR(e.date) = :year " +
            "GROUP BY e.user.id, month " +
            "ORDER BY MONTH(e.date) DESC")
    List<Object[]> getTotalYearReport(long userId, int year);
}
