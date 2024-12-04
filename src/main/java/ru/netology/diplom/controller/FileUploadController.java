package ru.netology.diplom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import ru.netology.diplom.CreateTable;


@Controller
@RequestMapping("/upload")
public class FileUploadController {

    @Autowired
    private ResourceLoader resourceLoader;

    private final CreateTable createTable;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FileUploadController(CreateTable createTable, JdbcTemplate jdbcTemplate) {
        this.createTable = createTable;
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping
    public String handleFileUpload(@RequestParam("files") MultipartFile[] files) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Получаем логин аутентифицированного пользователя

        createTable.createTable(username);

        for (MultipartFile file : files) {
            try {
                // Сохранение файла в таблицу, которая находится в схеме netology
                String sql = "INSERT INTO netology." + username + " (name, data, username) VALUES (?, ?, ?)";
                jdbcTemplate.update(sql, file.getOriginalFilename(), file.getBytes(), username);
            } catch (IOException e) {
                e.printStackTrace();
                return "Ошибка при загрузке файла: " + file.getOriginalFilename();
            }
        }
        System.out.println("Файлы загружены успешно!");
        return "redirect:/";
    }
}
