package ru.bezborodov.expensesbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bezborodov.expensesbot.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
