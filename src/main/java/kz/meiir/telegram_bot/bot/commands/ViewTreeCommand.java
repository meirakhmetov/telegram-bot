package kz.meiir.telegram_bot.bot.commands;

import kz.meiir.telegram_bot.repository.CategoryRepository;
import kz.meiir.telegram_bot.service.CategoryService;
import kz.meiir.telegram_bot.utils.TelegramBotUtils;

public class ViewTreeCommand implements BotCommand{
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;

    public ViewTreeCommand(CategoryService categoryService, CategoryRepository categoryRepository) {
        this.categoryService = categoryService;
        this.categoryRepository = categoryRepository;
    }
    @Override
    public void execute(Long chatId, String text) {
        if (categoryRepository.count() == 0) {
            TelegramBotUtils.sendMessage(chatId, "Категорий пока нет.");
        } else {
            String tree = categoryService.getCategoryTree();
            TelegramBotUtils.sendMessage(chatId, tree);
        }
    }
}
