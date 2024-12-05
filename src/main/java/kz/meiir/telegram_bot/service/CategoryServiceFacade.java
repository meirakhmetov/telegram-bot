package kz.meiir.telegram_bot.service;

import kz.meiir.telegram_bot.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Фасад для управления категориями.
 *
 * <p>Класс предоставляет унифицированный интерфейс для выполнения операций с категориями,
 * включая просмотр дерева категорий, добавление новых категорий и удаление существующих.</p>
 *
 * <h2>Основные задачи:</h2>
 * <ul>
 *     <li>Инкапсуляция логики работы с несколькими сервисами: {@link CategoryTreeService},
 *     {@link CategoryCreateService}, {@link CategoryDeleteService}.</li>
 *     <li>Обеспечение простого и единого доступа к функциональности работы с категориями.</li>
 * </ul>
 *
 * <p>Этот фасад упрощает использование сервисов для операций с категориями,
 * выступая в роли посредника между контроллером и соответствующими сервисами.</p>
 *
 * @author Meiir Akhmetov
 * @version 1.0
 */
@AllArgsConstructor
@Service
public class CategoryServiceFacade {

    private final CategoryTreeService categoryTreeService;
    private final CategoryCreateService categoryCreateService;
    private final CategoryDeleteService categoryDeleteService;

    /**
     * Возвращает иерархическую структуру категорий.
     *
     * <p>Метод делегирует выполнение операции сервису {@link CategoryTreeService}.</p>
     *
     * @return строковое представление дерева категорий.
     */
    public String getCategoryTree() {
        return categoryTreeService.getCategoryTree();
    }

    /**
     * Добавляет новую категорию.
     *
     * <p>Метод делегирует выполнение операции сервису {@link CategoryCreateService}.</p>
     *
     * @param elementName имя новой категории.
     * @param parentName  имя родительской категории. Может быть {@code null}, если категория создается на верхнем уровне.
     * @return сообщение о результате операции:
     *         <ul>
     *             <li>Успех: подтверждение добавления категории.</li>
     *             <li>Ошибка: причины невозможности добавления (некорректное имя, родитель не найден и т.д.).</li>
     *         </ul>
     */
    public String addCategory(String elementName, String parentName) {
        return categoryCreateService.addCategory(elementName, parentName);
    }

    /**
     * Удаляет указанную категорию.
     *
     * <p>Метод делегирует выполнение операции сервису {@link CategoryDeleteService}.</p>
     *
     * @param name имя категории или полный путь до неё (например: {@code "Родитель/ДочерняяКатегория"}).
     * @return сообщение о результате операции:
     *         <ul>
     *             <li>Успех: подтверждение удаления категории.</li>
     *             <li>Ошибка: причины невозможности удаления (некорректное имя, категория не найдена и т.д.).</li>
     *         </ul>
     */
    public String removeCategory(String name) {
        return categoryDeleteService.removeCategory(name);
    }
}