package kz.meiir.telegram_bot.service;

import kz.meiir.telegram_bot.model.Category;
import kz.meiir.telegram_bot.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class CategoryDeleteService {

    private final CategoryRepository categoryRepository;

    public String removeCategory(String name) {
        if (!isValidCategoryName(name)) {
            return "Ошибка: Название категории содержит недопустимые символы.";
        }

        String[] parts = name.split("/");

        if (parts.length == 0) {
            return "Ошибка: название категории пустое.";
        }

        Category parent = null;
        Category categoryToDelete = null;

        for (String part : parts) {
            categoryToDelete = (parent == null)
                    ? categoryRepository.findByNameAndParentIsNull(part.toLowerCase())
                    : categoryRepository.findByNameAndParent(part.toLowerCase(), parent);

            if (categoryToDelete == null) {
                return "Ошибка: Категория \"" + part + "\" не найдена.";
            }

            parent = categoryToDelete;
        }

        categoryRepository.delete(categoryToDelete);
        return "Категория \"" + name + "\" успешно удалена.";
    }

    private boolean isValidCategoryName(String name) {
        return name != null && name.matches("[a-zA-Zа-яА-ЯёЁ0-9\\s]+");
    }
}