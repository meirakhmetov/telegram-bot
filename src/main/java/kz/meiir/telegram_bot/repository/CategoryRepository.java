package kz.meiir.telegram_bot.repository;

import kz.meiir.telegram_bot.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Интерфейс {@code CategoryRepository} предоставляет методы для взаимодействия с
 * сущностью {@link Category} в базе данных.
 *
 * <h2>Описание:</h2>
 * Этот интерфейс расширяет {@link JpaRepository} и предоставляет дополнительные
 * методы для выполнения операций с категориями, такими как поиск и проверка
 * существования категории по имени.
 *
 * <h2>Методы:</h2>
 * <ul>
 *     <li><strong>findByNameAndParentIsNull</strong>: Находит категорию по имени,
 *     если у нее нет родителя.</li>
 *     <li><strong>findByNameAndParent</strong>: Находит категорию по имени и
 *     указанному родителю.</li>
 *     <li><strong>findByParentIsNull</strong>: Возвращает список всех категорий,
 *     у которых нет родителя.</li>
 *     <li><strong>findByName</strong>: Находит категорию по имени.</li>
 *     <li><strong>existsByName</strong>: Проверяет, существует ли категория с
 *     заданным именем.</li>
 * </ul>
 *
 * <h2>Использование:</h2>
 * Экземпляры этого интерфейса используются сервисным слоем приложения для
 * выполнения операций с данными категорий в базе данных.
 *
 * @author Meiir Akhmetov
 * @version 1.0
 */

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Находит категорию по имени, если у нее нет родителя.
     *
     * @param name имя категории
     * @return категория без родителя или null, если категория не найдена
     */
    Category findByNameAndParentIsNull(String name);

    /**
     * Находит категорию по имени и указанному родителю.
     *
     * @param name имя категории
     * @param parent родительская категория
     * @return категория с указанным именем и родителем или null, если категория не найдена
     */
    Category findByNameAndParent(String name, Category parent);

    /**
     * Возвращает список всех категорий, у которых нет родителя.
     *
     * @return список категорий без родителя
     */
    List<Category> findByParentIsNull();

    /**
     * Находит категорию по имени.
     *
     * @param name имя категории
     * @return категория с указанным именем или null, если категория не найдена
     */
    Category findByName(String name);

    /**
     * Проверяет, существует ли категория с заданным именем.
     *
     * @param name имя категории
     * @return true, если категория существует, иначе false
     */
    boolean existsByName(String name);

}
