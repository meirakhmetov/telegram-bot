package kz.meiir.telegram_bot.config;

import kz.meiir.telegram_bot.model.Category;
import kz.meiir.telegram_bot.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    public DataLoader(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) {
        if (categoryRepository.count() == 0) {
            Category root = new Category();
            root.setName("Root");
            categoryRepository.save(root);

            Category child1 = new Category();
            child1.setName("Child 1");
            child1.setParent(root);
            categoryRepository.save(child1);

            Category child2 = new Category();
            child2.setName("Child 2");
            child2.setParent(root);
            categoryRepository.save(child2);

            Category subChild = new Category();
            subChild.setName("SubChild 1");
            subChild.setParent(child1);
            categoryRepository.save(subChild);
        }
    }
}
