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

    private final CategoryRepository categoryRepository;

    /**
     * Получает иерархию категорий в виде строки.
     *
     * @return строка, представляющая иерархию категорий
     */
    public String getCategoryTree() {
        List<Category> rootCategories = categoryRepository.findByParentIsNull();
        StringBuilder treeBuilder = new StringBuilder();
        for (Category root : rootCategories) {
            buildTree(root, treeBuilder, 0);
        }
        return treeBuilder.toString();
    }

    /**
     * Рекурсивно строит строковое представление иерархии категорий.
     *
     * <p>Метод добавляет в {@link StringBuilder} название категории с отступом, соответствующим
     * уровню вложенности. Затем рекурсивно вызывает себя для всех дочерних категорий.</p>
     *
     * @param category текущая категория, для которой строится представление
     * @param builder {@link StringBuilder}, в который добавляется строковое представление
     * @param level уровень вложенности категории, используется для формирования отступов
     */
    private void buildTree(Category category, StringBuilder builder, int level) {
        builder.append("  ".repeat(level))
                .append("- ")
                .append(category.getName())
                .append("\n");

        for (Category child : category.getChildren()) {
            buildTree(child, builder, level + 1);
        }
    }

    /**
     * Добавляет новую категорию или иерархию категорий.
     *
     * @param elementName имя категории, которую необходимо добавить
     * @param parentName имя родительской категории
     * @return результат операции в виде сообщения
     */
    public String addCategory(String elementName, String parentName) {
        // Проверяем названия на корректность
        if (!isValidCategoryName(elementName) || (parentName != null && !isValidCategoryName(parentName))) {
            return "Ошибка: Название категории содержит недопустимые символы.";
        }

        // Проверяем, существует ли родительская категория, если она указана
        Category parent = parentName == null ? null : categoryRepository.findByName(parentName.toLowerCase());

        if (parentName != null && parent == null) {
            return "Ошибка: Родительская категория \"" + parentName + "\" не найдена.";
        }

        // Проверка на существование категории с таким же именем у указанного родителя
        if (parent != null) {
            if (categoryRepository.findByNameAndParent(elementName.toLowerCase(), parent) != null) {
                return "Ошибка: Категория с таким названием уже существует в родительской категории \"" + parentName + "\".";
            }
        } else {
            // Если родитель не указан, проверяем только на существование в верхнем уровне
            if (categoryRepository.existsByName(elementName.toLowerCase())) {
                return "Ошибка: Категория с таким названием уже существует.";
            }
        }

        // Создаем новую категорию
        Category newCategory = new Category();
        newCategory.setName(elementName.toLowerCase());
        newCategory.setParent(parent); // Устанавливаем родителя
        categoryRepository.save(newCategory);

        return "Категория добавлена успешно.";
    }

    /**
     * Удаляет категорию по имени.
     *
     * @param name имя категории, которую необходимо удалить
     * @return результат операции в виде сообщения
     */
    public String removeCategory(String name) {
        if (!isValidCategoryName(name)) {
            return "Ошибка: Название категории содержит недопустимые символы.";
        }

        // Разделяем путь категории по символу "/"
        String[] parts = name.split("/");

        if (parts.length == 0) {
            return "Ошибка: название категории пустое.";
        }

        Category parent = null;
        Category categoryToDelete = null;

        // Ищем категорию по имени с учётом родителя
        for (String part : parts) {
            categoryToDelete = (parent == null)
                    ? categoryRepository.findByNameAndParentIsNull(part.toLowerCase())
                    : categoryRepository.findByNameAndParent(part.toLowerCase(), parent);

            if (categoryToDelete == null) {
                return "Ошибка: Категория \"" + part + "\" не найдена.";
            }

            parent = categoryToDelete; // Переходим на следующий уровень
        }

        // Удаляем найденную категорию
        categoryRepository.delete(categoryToDelete);

        return "Категория \"" + name + "\" успешно удалена.";
    }

    /**
     * Проверяет допустимость названия категории.
     *
     * @param name название категории
     * @return true, если название допустимо; false в противном случае
     */
    private boolean isValidCategoryName(String name) {
        // Название может содержать только буквы, цифры и пробелы
        return name != null && name.matches("[a-zA-Zа-яА-ЯёЁ0-9\\s]+");
    }
}