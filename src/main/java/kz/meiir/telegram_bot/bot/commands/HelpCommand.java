package kz.meiir.telegram_bot.bot.commands;

import kz.meiir.telegram_bot.utils.TelegramBotUtils;

public class HelpCommand implements BotCommand{
    @Override
    public void execute(Long chatId, String text) {
        TelegramBotUtils.sendMessage(chatId, "/viewTree - Показать дерево категорий\n" +
                "/addElement <родительский элемент>/<дочерний элемент>\n" +
                "/removeElement <родительский элемент>/<дочерний элемент>\n" +
                "/download - Скачать дерево категорий в формате Excel\n" +
                "/upload - Загрузить дерево категорий из Excel");
    }
}
