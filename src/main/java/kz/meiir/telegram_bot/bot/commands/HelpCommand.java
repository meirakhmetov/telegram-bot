package kz.meiir.telegram_bot.bot.commands;

import kz.meiir.telegram_bot.utils.TelegramBotUtils;
/**
 * Класс {@code HelpCommand} представляет собой обработчик команды "/help".
 * <p>
 * При выполнении команда отправляет пользователю список доступных команд
 * с кратким описанием их функционала.
 * </p>
 *
 * <h2>Пример использования:</h2>
 * <pre>
 *     BotCommand helpCommand = new HelpCommand();
 *     helpCommand.execute(chatId, "/help");
 * </pre>
 *
 * @author Meiir Akhmetov
 * @version 1.0
 */
public class HelpCommand implements BotCommand{
    /**
     * Выполняет команду "/help", отправляя пользователю список доступных команд.
     *
     * @param chatId идентификатор чата, в который отправляется сообщение.
     * @param text   текст команды (в данном случае "/help").
     */
    @Override
    public void execute(Long chatId, String text) {
        TelegramBotUtils.sendMessage(chatId, "/viewTree - Показать дерево категорий\n" +
                "/addElement <родительский элемент>/<дочерний элемент>\n" +
                "/removeElement <родительский элемент>/<дочерний элемент>\n" +
                "/download - Скачать дерево категорий в формате Excel\n" +
                "/upload - Загрузить дерево категорий из Excel");
    }
}
