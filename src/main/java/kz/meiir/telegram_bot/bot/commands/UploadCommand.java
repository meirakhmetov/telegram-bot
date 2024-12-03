package kz.meiir.telegram_bot.bot.commands;

import kz.meiir.telegram_bot.utils.TelegramBotUtils;

import java.util.HashMap;
import java.util.Map;

public class UploadCommand implements BotCommand{
    private final Map<Long, Boolean> uploadMode;

    public UploadCommand(Map<Long, Boolean> uploadMode) {
        this.uploadMode = uploadMode;
    }

    @Override
    public void execute(Long chatId, String text) {
        uploadMode.put(chatId, true);
        TelegramBotUtils.sendMessage(chatId, "Теперь вы можете загрузить файл Excel с деревом категорий.");

    }
}
