package kz.meiir.telegram_bot.bot.commands;

import kz.meiir.telegram_bot.service.CategoryServiceFacade;
import kz.meiir.telegram_bot.utils.TelegramBotUtils;

/**
 * Команда {@code RemoveElementCommand} используется для удаления категории из дерева категорий.
 *
 * <h2>Описание:</h2>
 * Этот класс реализует функциональность обработки команды удаления категории, отправленной пользователем.
 *
 * <h2>Использование:</h2>
 * Эта команда вызывается при получении команды <code>/removeElement</code>.
 *
 * <h2>Пример вызова:</h2>
 * <pre>
 * new RemoveElementCommand(categoryService).execute(chatId, "/removeElement Категория");
 * </pre>
 *
 * @author Meiir Akhmetov
 * @version 1.0
 */

public class RemoveElementCommand implements BotCommand{
    private final CategoryServiceFacade categoryServiceFacade;

    /**
     * Конструктор для инициализации {@code RemoveElementCommand}.
     *
     * @param categoryServiceFacade сервис для управления категориями.
     */
    public RemoveElementCommand(CategoryServiceFacade categoryServiceFacade) {
        this.categoryServiceFacade = categoryServiceFacade;
    }

    /**
     * Выполняет команду удаления категории.
     *
     * @param chatId идентификатор чата, откуда пришла команда.
     * @param text   текст команды (например, "/removeElement Категория").
     */
    @Override
    public void execute(Long chatId, String text) {
        handleRemoveElementCommand(text, chatId);
    }
    /**
     * Обрабатывает команду удаления категории.
     *
     * <h2>Описание:</h2>
     * Проверяет правильность команды, извлекает название удаляемой категории
     * и вызывает соответствующий метод {@code CategoryService}.
     *
     * @param command текст команды, отправленный пользователем.
     * @param chatId  идентификатор чата, куда будет отправлен результат выполнения команды.
     */
    private void handleRemoveElementCommand(String command, Long chatId) {
        if (command.startsWith("/removeElement")) {
            // Извлекаем название категории из команды
            String elementName = command.substring("/removeElement".length()).trim();

            // Проверяем, указано ли название категории
            if (elementName.isEmpty()) {
                TelegramBotUtils.sendMessage(chatId, "Ошибка: Не указано название категории. Используйте:\n" +
                        "/removeElement <название категории>");
                return;
            }

            // Удаляем категорию через CategoryService
            String response = categoryServiceFacade.removeCategory(elementName);
            TelegramBotUtils.sendMessage(chatId, response);
        }
    }


}
