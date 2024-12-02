package kz.meiir.telegram_bot.bot;


import kz.meiir.telegram_bot.service.CategoryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramBotConfig extends TelegramLongPollingBot {
    private CategoryService categoryService;

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        // Обработка команд
        if (update.hasMessage() && update.getMessage().isCommand()) {
            String command = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            if ("/start".equals(command)) {
                sendMessage(chatId, "Добро пожаловать! Введите /help для списка команд.");
            }

            System.out.println("Получена команда: " + command);

            if ("/viewTree".equals(command)) {
                System.out.println("Обрабатываем команду /viewTree");
                String tree = categoryService.getCategoryTree();
                sendMessage(chatId, tree);
            }
        }
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}