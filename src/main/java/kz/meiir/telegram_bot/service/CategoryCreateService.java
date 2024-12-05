package kz.meiir.telegram_bot.service;

import kz.meiir.telegram_bot.model.Category;
import kz.meiir.telegram_bot.repository.CategoryRepository;
import kz.meiir.telegram_bot.validation.CategoryNameValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Сервис для добавления категорий в базу данных.
 *
 * <p>Этот класс отвечает за проверку корректности имен категорий,
 * валидацию существующих родительских категорий и добавление новых категорий
 * в иерархию с сохранением их в базе данных.</p>
 *
 * <h2>Основные функции:</h2>
 * <ul>
 *     <li>Валидация названий категорий.</li>
 *     <li>Проверка существования родительской категории (если указана).</li>
 *     <li>Добавление новой категории на верхний уровень или в подкатегорию.</li>
 * </ul>
 *
 * @author Meiir Akhmetov
 * @version 1.0
 */
@AllArgsConstructor
@Service
public class CategoryCreateService {

    private final CategoryRepository categoryRepository;

    /**
     * Добавляет новую категорию в базу данных.
     *
     * <p>Метод проводит валидацию имени категории, проверяет наличие
     * родительской категории (если указана) и добавляет новую категорию
     * на соответствующий уровень иерархии.</p>
     *
     * @param elementName имя новой категории
     * @param parentName  имя родительской категории (может быть {@code null})
     * @return сообщение о результате операции:
     *         <ul>
     *             <li>Успех: подтверждение добавления категории.</li>
     *             <li>Ошибка: причины невозможности добавления.</li>
     *         </ul>
     */
    public String addCategory(String elementName, String parentName) {
        // Проверка корректности имени новой категории
        if (CategoryNameValidator.isValidCategoryName(elementName)) {
            return "Ошибка: Название категории содержит недопустимые символы.";
        }

        // Проверка корректности имени родительской категории
        if (parentName != null && CategoryNameValidator.isValidCategoryName(elementName)) {
            return "Ошибка: Название родительской категории содержит недопустимые символы.";
        }

        // Поиск родительской категории в базе
        Category parent = parentName == null ? null : categoryRepository.findByName(parentName.toLowerCase());

        if (parentName != null && parent == null) {
            return "Ошибка: Родительская категория \"" + parentName + "\" не найдена.";
        }

        // Проверка существования категории на соответствующем уровне
        boolean categoryExists = parent != null
                ? categoryRepository.findByNameAndParent(elementName.toLowerCase(), parent) != null
                : categoryRepository.existsByName(elementName.toLowerCase());

        if (categoryExists) {
            return parent != null
                    ? "Ошибка: Категория с таким названием уже существует в родительской категории \"" + parentName + "\"."
                    : "Ошибка: Категория с таким названием уже существует на верхнем уровне.";
        }

        // Создание и сохранение новой категории
        Category newCategory = new Category();
        newCategory.setName(elementName.toLowerCase());
        newCategory.setParent(parent);

        categoryRepository.save(newCategory);

        return "Категория \"" + elementName + "\" успешно добавлена"
                + (parent != null ? " в родительскую категорию \"" + parentName + "\"." : ".");
    }
}
