package kz.meiir.telegram_bot.validation;
/**
 * Утилитный класс для проверки корректности названий категорий.
 *
 * <p>Этот класс предоставляет статический метод для валидации
 * имен категорий. Названия проверяются на соответствие заданному
 * регулярному выражению, которое допускает использование букв,
 * цифр, пробелов и символов кириллицы и латиницы.</p>
 *
 * <p>Класс содержит приватный конструктор, чтобы исключить
 * возможность создания его экземпляров, так как он предназначен
 * исключительно для работы со статическими методами.</p>
 *
 * @author Meiir Akhmetov
 * @version 1.0
 */
public class CategoryNameValidator {
    /**
     * Приватный конструктор для запрета создания экземпляров.
     */
    private CategoryNameValidator() {
        // Приватный конструктор, чтобы запретить создание экземпляра
    }

    /**
     * Проверяет, соответствует ли имя категории заданным требованиям.
     *
     * <p>Имя считается некорректным, если оно:
     * <ul>
     *     <li>является {@code null};</li>
     *     <li>не соответствует регулярному выражению {@code "[a-zA-Zа-яА-ЯёЁ0-9\\s]+"}.</li>
     * </ul>
     * </p>
     *
     * @param categoryName название категории, которое нужно проверить.
     * @return {@code true}, если имя некорректно, иначе {@code false}.
     */
    public static boolean isValidCategoryName(String categoryName) {
        return categoryName == null || !categoryName.matches("[a-zA-Zа-яА-ЯёЁ0-9\\s]+");
    }
}
