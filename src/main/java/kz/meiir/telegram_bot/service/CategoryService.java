package kz.meiir.telegram_bot.service;

import kz.meiir.telegram_bot.model.Category;
import kz.meiir.telegram_bot.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

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
                    ? categoryRepository.findByNameAndParentIsNull(part)
                    : categoryRepository.findByNameAndParent(part, parent);

            if (existingCategory != null) {
                parent = existingCategory; // Если категория уже существует, переходим к ней
            } else {
                // Создаем новую категорию
                Category newCategory = new Category();
                newCategory.setName(part);
                newCategory.setParent(parent); // Устанавливаем родителя
                categoryRepository.save(newCategory);
                parent = newCategory; // Теперь это текущий родитель
            }
        }

        return "Категория(и) добавлены успешно.";
    }

    // Вспомогательный метод для добавления простой категории
    private String addCategorySimple(String elementName, String parentName) {
        if (categoryRepository.existsByName(elementName)) {
            return "Категория с таким названием уже существует.";
        }

        Category parent = parentName == null ? null : categoryRepository.findByName(parentName);

        Category newCategory = new Category();
        newCategory.setName(elementName);
        newCategory.setParent(parent);

        categoryRepository.save(newCategory);
        return "Категория добавлена успешно.";
    }

    public String removeCategory(String name) {
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
                    ? categoryRepository.findByNameAndParentIsNull(part)
                    : categoryRepository.findByNameAndParent(part, parent);

            if (categoryToDelete == null) {
                return "Ошибка: Категория \"" + part + "\" не найдена.";
            }

            parent = categoryToDelete; // Переходим на следующий уровень
        }

        // Удаляем найденную категорию
        categoryRepository.delete(categoryToDelete);

        return "Категория \"" + name + "\" успешно удалена.";
    }
}