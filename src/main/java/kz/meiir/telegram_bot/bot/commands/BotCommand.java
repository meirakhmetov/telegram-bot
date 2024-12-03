package kz.meiir.telegram_bot.bot.commands;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface BotCommand {
    void execute(Long chatId, String text);
}
