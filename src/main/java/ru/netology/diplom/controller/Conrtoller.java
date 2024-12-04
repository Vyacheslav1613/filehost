package ru.netology.diplom.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.netology.diplom.CreateTable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Conrtoller {

    private String user;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CreateTable createTable; // Инжектируем CreateTable

    @GetMapping("/upload")
    public String getUpload() {
        return "upload.html";
    }

    @GetMapping("/")
    public String listFiles(Model model) {
        System.out.println("/ начал работать");
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("Пользователь: " + authentication.getName());
            String username = authentication.getName(); // Получаем имя аутентифицированного пользователя
            user = username;
            System.out.println("Имя пользователя: " + username);
            try {
                createTable.createTable(username);
                System.out.println("Создали таблицу? ");
            } catch (Exception e) {
                System.out.println("Ошибка при создании таблицы: " + e.getMessage());
            }

            // Динамически формируем SQL-запрос для извлечения файлов из таблицы с именем пользователя
            String sql = "SELECT name FROM netology." + username; // Предполагается, что столбец называется name
            List<String> files = jdbcTemplate.queryForList(sql, String.class);

            // Добавляем список файлов в модель
            model.addAttribute("files", files);
            System.out.println("Создали млм таблица существоала");
            return "fileList.html";
        } catch (Exception e) {
            System.out.println("Таблицы не существует");
            // Возвращаем страницу ошибки или другую подходящую страницу
            return "error"; // Убедитесь, что у вас есть шаблон error.html
        }
    }

    @GetMapping("/delete/{nameFile}")
    public String deleteFile(@PathVariable String nameFile) {
        jdbcTemplate.update("DELETE FROM netology." +  user + "  WHERE name = '" + nameFile + "'");
        return "redirect:/";
    }

    @GetMapping(value = "/files/{nameFile}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getFile(@PathVariable String nameFile) throws IOException {
        String sql = "SELECT data FROM netology." + user + " WHERE name = ?";

        try {
            byte[] fileData = jdbcTemplate.queryForObject(sql, new Object[]{nameFile}, byte[].class);

            if (fileData == null) {
                throw new FileNotFoundException("Файл не найден");
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG); // Установите нужный Content-Type
            return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(
                    ("Ошибка при извлечении файла: " + e.getMessage()).getBytes(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}