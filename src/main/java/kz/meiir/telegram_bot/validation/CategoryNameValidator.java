package kz.meiir.telegram_bot.validation;

public class CategoryNameValidator {
    private CategoryNameValidator() {
        // Приватный конструктор, чтобы запретить создание экземпляра
    }

    public static boolean isValidCategoryName(String categoryName) {
        return categoryName == null || !categoryName.matches("[a-zA-Zа-яА-ЯёЁ0-9\\s]+");
    }
}
