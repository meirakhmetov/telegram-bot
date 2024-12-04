package kz.meiir.telegram_bot.bot;

import kz.meiir.telegram_bot.bot.commands.*;
import kz.meiir.telegram_bot.config.TelegramBotProperties;
import kz.meiir.telegram_bot.repository.CategoryRepository;
import kz.meiir.telegram_bot.service.CategoryService;
import kz.meiir.telegram_bot.utils.TelegramBotUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TelegramBotConfig extends TelegramLongPollingBot {
    private final TelegramBotProperties botProperties;

    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;
    private final Map<Long, Boolean> uploadMode = new HashMap<>();

    @Override
    public String getBotUsername() {
        return botProperties.getUsername();
    }

    @Override
    public String getBotToken() {
        return botProperties.getToken();
    }



    @Override
    public void onUpdateReceived(Update update) {
        TelegramBotUtils.setBot(this);

        if (!update.hasMessage()) return;

        Long chatId = update.getMessage().getChatId();

        if (update.getMessage().isCommand()) {
            handleCommand(update, chatId);
        } else if (update.getMessage().hasDocument()) {
            try {
                handleUploadCommand(update, chatId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void handleCommand(Update update, Long chatId) {
        String command = update.getMessage().getText();

        Map<String, BotCommand> commandHandlers = Map.of(
                "/start", new StartCommand(),
                "/viewTree", new ViewTreeCommand(categoryService, categoryRepository),
                "/help", new HelpCommand(),
                "/download", new DownloadCommand(categoryRepository),
                "/upload", new UploadCommand(uploadMode, categoryService)
        );

        BotCommand handler = commandHandlers.get(command);
        if (handler != null) {
            handler.execute(chatId, command);
        } else {
            new AddElementCommand(categoryService).execute(chatId, command);
            new RemoveElementCommand(categoryService).execute(chatId, command);
        }
    }

    private void handleUploadCommand(Update update, Long chatId) throws Exception {
        if (uploadMode.getOrDefault(chatId, false)) {
            new UploadCommand(uploadMode, categoryService).handleDocument(update);
        } else {
            TelegramBotUtils.sendMessage(chatId, "Сначала используйте команду /upload, чтобы загрузить файл.");
        }
    }
}
