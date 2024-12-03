package kz.meiir.telegram_bot.service;

import kz.meiir.telegram_bot.model.Category;
import kz.meiir.telegram_bot.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;


    public String getCategoryTree() {
        List<Category> rootCategories = categoryRepository.findByParentIsNull();
        StringBuilder treeBuilder = new StringBuilder();

        for (Category root : rootCategories) {
            buildTree(root, treeBuilder, 0);
        }

        return treeBuilder.toString();
    }


    private void buildTree(Category category, StringBuilder builder, int level) {
        builder.append("  ".repeat(level))
                .append("- ")
                .append(category.getName())
                .append("\n");

        for (Category child : category.getChildren()) {
            buildTree(child, builder, level + 1);
        }
    }

    public String addCategory(String categoryPath, String parentName) {
        // Проверяем названия на корректность
        if (!isValidCategoryName(categoryPath) || (parentName != null && !isValidCategoryName(parentName))) {
            return "Ошибка: Название категории содержит недопустимые символы.";
        }
        // Разделяем путь по символу "/"
        String[] parts = categoryPath.split("/");

        // Если путь состоит из одной части, работаем как раньше
        if (parts.length == 1) {
            return addCategorySimple(parts[0].trim(), parentName);
        }

        // Если путь содержит несколько частей, создаем иерархию
        Category parent = null;

        for (String part : parts) {
            part = part.trim(); // Убираем лишние пробелы
            if (part.isEmpty()) continue;

            // Проверяем, существует ли категория с таким именем у текущего родителя
            Category existingCategory = (parent == null)
                    ? categoryRepository.findByNameAndParentIsNull(part.toLowerCase())
                    : categoryRepository.findByNameAndParent(part.toLowerCase(), parent);

            if (existingCategory != null) {
                parent = existingCategory; // Если категория уже существует, переходим к ней
            } else {
                // Создаем новую категорию
                Category newCategory = new Category();
                newCategory.setName(part.toLowerCase());
                newCategory.setParent(parent); // Устанавливаем родителя
                categoryRepository.save(newCategory);
                parent = newCategory; // Теперь это текущий родитель
            }
        }

        return "Категория(и) добавлены успешно.";
    }

    // Вспомогательный метод для добавления простой категории
    private String addCategorySimple(String elementName, String parentName) {
        // Проверяем названия на корректность
        if (!isValidCategoryName(elementName) || (parentName != null && !isValidCategoryName(parentName))) {
            return "Ошибка: Название категории содержит недопустимые символы.";
        }
        if (categoryRepository.existsByName(elementName.toLowerCase())) {
            return "Категория с таким названием уже существует.";
        }

        Category parent = parentName == null ? null : categoryRepository.findByName(parentName.toLowerCase());

        Category newCategory = new Category();
        newCategory.setName(elementName.toLowerCase());
        newCategory.setParent(parent);

        categoryRepository.save(newCategory);
        return "Категория добавлена успешно.";
    }

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

    // Метод для проверки допустимости названия категории
    private boolean isValidCategoryName(String name) {
        // Название может содержать только буквы, цифры и пробелы
        return name != null && name.matches("[a-zA-Zа-яА-ЯёЁ0-9\\s]+");
    }
}