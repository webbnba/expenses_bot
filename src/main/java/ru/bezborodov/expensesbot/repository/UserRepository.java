package ru.bezborodov.expensesbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bezborodov.expensesbot.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
