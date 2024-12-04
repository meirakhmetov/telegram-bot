package kz.meiir.telegram_bot.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс {@code Category} представляет собой модель категории для бота.
 *
 * <h2>Описание:</h2>
 * Этот класс используется для представления категорий в дереве категорий бота.
 * Каждая категория может иметь родительскую категорию и список дочерних категорий.
 *
 * <h2>Свойства:</h2>
 * <ul>
 *     <li><strong>id</strong>: Уникальный идентификатор категории.</li>
 *     <li><strong>name</strong>: Название категории.</li>
 *     <li><strong>parent</strong>: Родительская категория, к которой принадлежит данная категория.</li>
 *     <li><strong>children</strong>: Список дочерних категорий.</li>
 * </ul>
 *
 * <h2>Связи:</h2>
 * <ul>
 *     <li>Множественная связь с дочерними категориями с использованием аннотации {@code @OneToMany}.</li>
 *     <li>Одинарная связь с родительской категорией с использованием аннотации {@code @ManyToOne}.</li>
 * </ul>
 *
 * <h2>Использование:</h2>
 * Экземпляры этого класса используются для создания иерархии категорий, которая может быть
 * использована в интерфейсе бота для взаимодействия с пользователями.
 *
 * @author Meiir Akhmetov
 * @version 1.0
 */

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Уникальный идентификатор категории

    private String name; // Название категории

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent; // Родительская категория


    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Category> children = new ArrayList<>(); // Список дочерних категорий
}
