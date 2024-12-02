package kz.meiir.telegram_bot.repository;

import kz.meiir.telegram_bot.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Поиск категории по имени и отсутствию родителя
    Category findByNameAndParentIsNull(String name);

    // Поиск категории по имени и указанному родителю
    Category findByNameAndParent(String name, Category parent);

    List<Category> findByParentIsNull();

    Category findByName(String name);



    boolean existsByName(String name);

}
