package ru.netology.diplom.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@Entity
@Table(name = "images") // Укажите имя таблицы в базе данных
public class Images {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type;

    @Lob
    private byte[] data;


    public Images() {
    }

    public Images(String name, String type, byte[] data) {
        this.name = name;
        this.type = type;
        this.data = data;
    }

}

