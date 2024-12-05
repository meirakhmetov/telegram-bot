package kz.meiir.telegram_bot.service;

import kz.meiir.telegram_bot.model.Category;
import kz.meiir.telegram_bot.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class CategoryCreateService {

    private final CategoryRepository categoryRepository;

    public String addCategory(String elementName, String parentName) {
        if (!isValidCategoryName(elementName)) {
            return "Ошибка: Название категории содержит недопустимые символы.";
        }

        if (parentName != null && !isValidCategoryName(parentName)) {
            return "Ошибка: Название родительской категории содержит недопустимые символы.";
        }

        Category parent = parentName == null ? null : categoryRepository.findByName(parentName.toLowerCase());

        if (parentName != null && parent == null) {
            return "Ошибка: Родительская категория \"" + parentName + "\" не найдена.";
        }

        boolean categoryExists = parent != null
                ? categoryRepository.findByNameAndParent(elementName.toLowerCase(), parent) != null
                : categoryRepository.existsByName(elementName.toLowerCase());

        if (categoryExists) {
            return parent != null
                    ? "Ошибка: Категория с таким названием уже существует в родительской категории \"" + parentName + "\"."
                    : "Ошибка: Категория с таким названием уже существует на верхнем уровне.";
        }

        Category newCategory = new Category();
        newCategory.setName(elementName.toLowerCase());
        newCategory.setParent(parent);

        categoryRepository.save(newCategory);

        return "Категория \"" + elementName + "\" успешно добавлена"
                + (parent != null ? " в родительскую категорию \"" + parentName + "\"." : ".");
    }

    private boolean isValidCategoryName(String name) {
        return name != null && name.matches("[a-zA-Zа-яА-ЯёЁ0-9\\s]+");
    }
}
