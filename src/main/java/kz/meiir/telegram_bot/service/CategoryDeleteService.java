package kz.meiir.telegram_bot.service;

import kz.meiir.telegram_bot.model.Category;
import kz.meiir.telegram_bot.repository.CategoryRepository;
import kz.meiir.telegram_bot.validation.CategoryNameValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Сервис для удаления категорий из базы данных.
 *
 * <p>Этот класс отвечает за удаление категорий из базы данных.
 * Удаление осуществляется по указанному имени категории, с учетом вложенности
 * и проверки корректности имени категории.</p>
 *
 * <h2>Основные функции:</h2>
 * <ul>
 *     <li>Валидация имени категории.</li>
 *     <li>Поиск категории по имени и иерархии.</li>
 *     <li>Удаление категории из базы данных.</li>
 * </ul>
 *
 * @author Meiir Akhmetov
 * @version 1.0
 */
@AllArgsConstructor
@Service
public class CategoryDeleteService {

    private final CategoryRepository categoryRepository;

    /**
     * Удаляет категорию из базы данных.
     *
     * <p>Метод позволяет удалить категорию по её имени, которое может включать
     * иерархический путь через символ "/". Проводится проверка корректности
     * имени, поиск категории в указанной иерархии, а затем её удаление.</p>
     *
     * @param name имя категории или полный путь до неё, например: {@code "Родитель/ДочерняяКатегория"}.
     * @return сообщение о результате операции:
     *         <ul>
     *             <li>Успех: подтверждение удаления категории.</li>
     *             <li>Ошибка: причины невозможности удаления (некорректное имя, категория не найдена и т.д.).</li>
     *         </ul>
     */
    public String removeCategory(String name) {
        if (CategoryNameValidator.isValidCategoryName(name)) {
            return "Ошибка: Название категории содержит недопустимые символы.";
        }

        // Разделение пути категории по символу "/"
        String[] parts = name.split("/");

        if (parts.length == 0) {
            return "Ошибка: название категории пустое.";
        }

        Category parent = null;
        Category categoryToDelete = null;

        // Поиск категории по имени с учетом иерархии
        for (String part : parts) {
            categoryToDelete = (parent == null)
                    ? categoryRepository.findByNameAndParentIsNull(part.toLowerCase())
                    : categoryRepository.findByNameAndParent(part.toLowerCase(), parent);

            if (categoryToDelete == null) {
                return "Ошибка: Категория \"" + part + "\" не найдена.";
            }

            parent = categoryToDelete; // Переход на следующий уровень
        }

        // Удаление найденной категории
        categoryRepository.delete(categoryToDelete);
        return "Категория \"" + name + "\" успешно удалена.";
    }

}