package kz.meiir.telegram_bot.service;

import kz.meiir.telegram_bot.model.Category;
import kz.meiir.telegram_bot.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * Сервис для работы с деревом категорий.
 *
 * <p>Класс отвечает за построение иерархической структуры категорий,
 * представляя её в виде строки. Использует репозиторий {@link CategoryRepository}
 * для получения данных из базы.</p>
 *
 * <h2>Основные задачи:</h2>
 * <ul>
 *     <li>Получение списка корневых категорий (категорий без родителя).</li>
 *     <li>Построение дерева категорий в виде многоуровневой текстовой структуры.</li>
 * </ul>
 *
 * @author Meiir Akhmetov
 * @version 1.0
 */
@AllArgsConstructor
@Service
public class CategoryTreeService {

    private final CategoryRepository categoryRepository;

    /**
     * Возвращает иерархическую структуру категорий.
     *
     * <p>Метод извлекает из базы корневые категории и рекурсивно строит дерево
     * в виде строки. Каждый уровень дерева представлен отступами.</p>
     *
     * @return строковое представление дерева категорий.
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
     * Рекурсивно строит текстовое представление дерева категорий.
     *
     * @param category категория, с которой начинается построение.
     * @param builder  объект {@link StringBuilder}, в который записывается дерево.
     * @param level    текущий уровень глубины дерева (используется для создания отступов).
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
}
