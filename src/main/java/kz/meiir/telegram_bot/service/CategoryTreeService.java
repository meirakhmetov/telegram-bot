package kz.meiir.telegram_bot.service;

import kz.meiir.telegram_bot.model.Category;
import kz.meiir.telegram_bot.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class CategoryTreeService {

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
}
