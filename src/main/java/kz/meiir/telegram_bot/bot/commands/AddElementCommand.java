package kz.meiir.telegram_bot.bot.commands;

import kz.meiir.telegram_bot.service.CategoryService;
import kz.meiir.telegram_bot.utils.TelegramBotUtils;

public class AddElementCommand implements BotCommand{
    private final CategoryService categoryService;

    public AddElementCommand(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public void execute(Long chatId, String text) {
        handleAddElementCommand(text, chatId);
    }

    private void handleAddElementCommand(String command, Long chatId) {
        if (command.startsWith("/addElement")) {
            String[] args = command.split(" ", 2); // Разделяем команду и параметры

            if (args.length == 2) { // Проверяем, есть ли параметры
                String[] parts = args[1].split("/", 2); // Разделяем параметры по "/"

                if (parts.length == 2) {
                    String parentName = parts[0].trim(); // Родительский элемент
                    String elementName = parts[1].trim(); // Дочерний элемент
                    String response = categoryService.addCategory(elementName, parentName);
                    TelegramBotUtils.sendMessage(chatId, response);
                } else if (parts.length == 1) {
                    // Если только один элемент без родительского
                    String elementName = parts[0].trim();
                    String response = categoryService.addCategory(elementName, null);
                    TelegramBotUtils.sendMessage(chatId, response);
                } else {
                    TelegramBotUtils.sendMessage(chatId, "Неверный формат команды. Используйте:\n" +
                            "/addElement <родительский элемент>/<дочерний элемент>");
                }
            } else {
                TelegramBotUtils.sendMessage(chatId, "Неверный формат команды. Используйте:\n" +
                        "/addElement <родительский элемент>/<дочерний элемент>");
            }
        }
    }
}
