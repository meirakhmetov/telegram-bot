package kz.meiir.telegram_bot.bot.commands;

import kz.meiir.telegram_bot.repository.CategoryRepository;
import kz.meiir.telegram_bot.service.CategoryServiceFacade;
import kz.meiir.telegram_bot.utils.TelegramBotUtils;

/**
 * Команда {@code ViewTreeCommand} используется для отображения дерева категорий в Telegram-боте.
 *
 * <h2>Описание:</h2>
 * Этот класс получает данные о категориях из базы данных и формирует текстовое представление дерева категорий,
 * которое затем отправляется пользователю в чате.
 *
 * <h2>Использование:</h2>
 * Пользователь может выполнить команду <code>/viewTree</code>, чтобы увидеть текущее состояние дерева категорий.
 *
 * @author Meiir Akhmetov
 * @version 1.0
 */
public class ViewTreeCommand implements BotCommand{
    private final CategoryServiceFacade categoryServiceFacade;
    private final CategoryRepository categoryRepository;
    /**
     * Конструктор для создания экземпляра {@code ViewTreeCommand}.
     *
     * @param categoryServiceFacade Сервис для работы с категориями.
     * @param categoryRepository Репозиторий для доступа к данным категорий.
     */
    public ViewTreeCommand(CategoryServiceFacade categoryServiceFacade, CategoryRepository categoryRepository) {
        this.categoryServiceFacade = categoryServiceFacade;
        this.categoryRepository = categoryRepository;
    }
    /**
     * Выполняет команду отображения дерева категорий.
     *
     * Если категорий нет, отправляет сообщение об отсутствии категорий.
     * Если категории есть, получает дерево категорий и отправляет его пользователю.
     *
     * @param chatId идентификатор чата, откуда пришла команда.
     * @param text текст команды (например, "/viewTree").
     */
    @Override
    public void execute(Long chatId, String text) {
        if (categoryRepository.count() == 0) {
            TelegramBotUtils.sendMessage(chatId, "Категорий пока нет.");
        } else {
            String tree = categoryServiceFacade.getCategoryTree();
            TelegramBotUtils.sendMessage(chatId, tree);
        }
    }
}
