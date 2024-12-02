package kz.meiir.telegram_bot.service;

import kz.meiir.telegram_bot.model.Category;
import kz.meiir.telegram_bot.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public String addCategory(String name, String parentName) {
        if (name == null || name.trim().isEmpty()) {
            return "Название элемента не может быть пустым.";
        }

        if (parentName == null) {
            // Добавляем корневую категорию
            if (categoryRepository.existsByName(name)) {
                return "Элемент с таким названием уже существует.";
            }

            Category category = new Category();
            category.setName(name);
            categoryRepository.save(category);

            return "Корневой элемент \"" + name + "\" успешно добавлен.";
        } else {
            // Добавляем дочернюю категорию
            Optional<Category> parent = categoryRepository.findByName(parentName);
            if (parent.isEmpty()) {
                return "Родительский элемент \"" + parentName + "\" не найден.";
            }

            if (categoryRepository.existsByName(name)) {
                return "Элемент с таким названием уже существует.";
            }

            Category category = new Category();
            category.setName(name);
            category.setParent(parent.get());
            categoryRepository.save(category);

            return "Элемент \"" + name + "\" успешно добавлен к родителю \"" + parentName + "\".";
        }
    }
}