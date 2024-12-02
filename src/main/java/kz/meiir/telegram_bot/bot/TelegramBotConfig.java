package kz.meiir.telegram_bot.bot;

import kz.meiir.telegram_bot.service.CategoryService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
public class TelegramBotConfig extends TelegramLongPollingBot {
    private final CategoryService categoryService;

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Getter
    @Value("${telegram.bot.token}")
    private String botToken;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().isCommand()) {
            String command = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            if ("/start".equals(command)) {
                sendMessage(chatId, "Добро пожаловать! Введите /help для списка команд.");
            }

            if ("/viewTree".equals(command)) {
                String tree = categoryService.getCategoryTree();
                sendMessage(chatId, tree != null ? tree : "Дерево категорий пусто.");
            }

            if ("/help".equals(command)) {
                sendMessage(chatId, "/viewTree - Показать дерево категорий\n"
                        + "/addElement <название> - Добавить элемент");
            }

            if (command.startsWith("/addElement")) {
                String[] parts = command.split(" ", 3); // Разделяем команду
                if (parts.length == 2) {
                    String elementName = parts[1];
                    String response = categoryService.addCategory(elementName, null);
                    sendMessage(chatId, response);
                } else if (parts.length == 3) {
                    String parentName = parts[1];
                    String elementName = parts[2];
                    String response = categoryService.addCategory(elementName, parentName);
                    sendMessage(chatId, response);
                } else {
                    sendMessage(chatId, "Неверный формат команды. Используйте:\n" +
                            "/addElement <название элемента>\n" +
                            "/addElement <родительский элемент> <дочерний элемент>");
                }
            }
        }
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString()); // Чат ID должен быть строкой
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            System.err.println("Ошибка отправки сообщения: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}