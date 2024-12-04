package ru.netology.diplom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CreateTable {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CreateTable(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createTable(String tableName) {
        System.out.println("Запустили создание таблицы: " + tableName);

        try {
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS netology." + tableName + " (id SERIAL PRIMARY KEY, " +
                    "name VARCHAR(255), " +
                    "data BYTEA, " +
                    "username VARCHAR(255), " +
                    "uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
            System.out.println("Таблица " + tableName + " создана");
        } catch (Exception e) {
            System.out.println("Ошибка при создании таблицы: " + e.getMessage());
        }
    }
}