package ru.bezborodov.expensesbot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Entity(name = "Users")
@Data
public class User {

    @Id
    private Long id;
    private String firstName;
    private String lastName;
    private String userName;
    private LocalDateTime registeredAt;
}
