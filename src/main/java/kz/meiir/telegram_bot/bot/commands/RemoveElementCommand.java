package kz.meiir.telegram_bot.bot.commands;

import kz.meiir.telegram_bot.service.CategoryService;
import kz.meiir.telegram_bot.utils.TelegramBotUtils;

public class RemoveElementCommand implements BotCommand{
    private final CategoryService categoryService;

    public RemoveElementCommand(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public void execute(Long chatId, String text) {
        handleRemoveElementCommand(text, chatId);
    }
    private void handleRemoveElementCommand(String command, Long chatId) {
        if (command.startsWith("/removeElement")) {
            // Убираем команду из текста и получаем только название
            String elementName = command.substring("/removeElement".length()).trim();

            if (elementName.isEmpty()) {
                TelegramBotUtils.sendMessage(chatId, "Ошибка: Не указано название категории. Используйте:\n" +
                        "/removeElement <название категории>");
                return;
            }

            // Удаляем категорию
            String response = categoryService.removeCategory(elementName);
            TelegramBotUtils.sendMessage(chatId, response);
        }
    }


}
