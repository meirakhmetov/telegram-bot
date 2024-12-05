package kz.meiir.telegram_bot.service;

import kz.meiir.telegram_bot.model.Category;
import kz.meiir.telegram_bot.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис {@code CategoryService} для управления категориями.
 *
 * <h2>Описание:</h2>
 * Данный сервис предоставляет методы для добавления, удаления и получения иерархии категорий.
 * Он взаимодействует с {@link CategoryRepository} для выполнения операций с базой данных.
 *
 * <h2>Методы:</h2>
 * <ul>
 *     <li><strong>getCategoryTree</strong>: Возвращает иерархическую строку категорий.</li>
 *     <li><strong>addCategory</strong>: Добавляет новую категорию или иерархию категорий.</li>
 *     <li><strong>removeCategory</strong>: Удаляет категорию по имени.</li>
 *     <li><strong>isValidCategoryName</strong>: Проверяет корректность названия категории.</li>
 * </ul>
 *
 * @author Meiir Akhmetov
 * @version 1.0
 */
@AllArgsConstructor
@Service
public class CategoryService {

    private final CategoryTreeService categoryTreeService;
    private final CategoryCreateService categoryCreateService;
    private final CategoryDeleteService categoryDeleteService;

    public String getCategoryTree() {
        return categoryTreeService.getCategoryTree();
    }

    public String addCategory(String elementName, String parentName) {
        return categoryCreateService.addCategory(elementName, parentName);
    }

    public String removeCategory(String name) {
        return categoryDeleteService.removeCategory(name);
    }
}