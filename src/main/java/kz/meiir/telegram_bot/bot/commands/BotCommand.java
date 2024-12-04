package kz.meiir.telegram_bot.bot.commands;
/**
 * Интерфейс {@code BotCommand} представляет собой контракт для всех команд Telegram-бота.
 * <p>
 * Все команды, реализующие этот интерфейс, должны определить метод {@code execute},
 * который отвечает за выполнение команды.
 * </p>
 *
 * <h2>Пример использования:</h2>
 * <pre>
 * public class StartCommand implements BotCommand {
 *     @Override
 *     public void execute(Long chatId, String text) {
 *         // Логика команды /start
 *     }
 * }
 * </pre>
 *
 * <h2>Основные реализации:</h2>
 * <ul>
 *     <li>{@code StartCommand}</li>
 *     <li>{@code HelpCommand}</li>
 *     <li>{@code AddElementCommand}</li>
 *     <li>{@code RemoveElementCommand}</li>
 * </ul>
 *
 * @author Meiir Akhmetov
 * @version 1.0
 */
public interface BotCommand {
    /**
     * Выполняет команду Telegram-бота.
     *
     * @param chatId идентификатор чата, в который отправляется результат выполнения команды.
     * @param text   текст команды, содержащий ключевое слово команды и возможные параметры.
     */
    void execute(Long chatId, String text);
}
