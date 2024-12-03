package kz.meiir.telegram_bot.bot.commands;

import kz.meiir.telegram_bot.utils.TelegramBotUtils;
import org.telegram.telegrambots.meta.api.objects.Update;

public class StartCommand implements BotCommand{
    @Override
    public void execute(Long chatId, String text) {
        TelegramBotUtils.sendMessage(chatId, "Добро пожаловать! Введите /help для списка команд.");

    }
}
