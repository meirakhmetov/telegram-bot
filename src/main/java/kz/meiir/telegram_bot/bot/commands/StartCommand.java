package kz.meiir.telegram_bot.bot.commands;

import kz.meiir.telegram_bot.utils.TelegramBotUtils;

/**
 * Команда {@code StartCommand} используется для приветствия пользователя при первом взаимодействии с ботом.
 *
 * <h2>Описание:</h2>
 * Этот класс отвечает за обработку команды <code>/start</code>, отправляя пользователю
 * приветственное сообщение с указанием, как получить список доступных команд.
 *
 * <h2>Использование:</h2>
 * Команда вызывается, когда пользователь вводит <code>/start</code>.
 *
 * <h2>Пример вызова:</h2>
 * <pre>
 * new StartCommand().execute(chatId, "/start");
 * </pre>
 *
 * @author Meiir Akhmetov
 * @version 1.0
 */
public class StartCommand implements BotCommand{
    /**
     * Выполняет команду приветствия.
     *
     * <h2>Описание:</h2>
     * Отправляет пользователю сообщение, приветствующее его в боте,
     * и предлагает воспользоваться командой <code>/help</code> для получения списка доступных команд.
     *
     * @param chatId идентификатор чата, откуда пришла команда.
     * @param text   текст команды (например, "/start").
     */
    @Override
    public void execute(Long chatId, String text) {
        TelegramBotUtils.sendMessage(chatId, "Добро пожаловать! Введите /help для списка команд.");

    }
}
