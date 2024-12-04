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
/**
 * TelegramBotConfig — основной класс, реализующий логику работы Telegram-бота.
 * <p>
 * Этот класс наследуется от {@link TelegramLongPollingBot} и обрабатывает входящие обновления,
 * включая команды и загрузку документов. Логика команд делегируется соответствующим классам
 * команд, реализующим интерфейс {@code BotCommand}.
 * </p>
 *
 * <h2>Основные функции:</h2>
 * <ul>
 *     <li>Обработка команд, отправленных пользователями.</li>
 *     <li>Загрузка и обработка документов.</li>
 *     <li>Интеграция с сервисами для управления категориями.</li>
 * </ul>
 *
 * @author Meiir Akhmetov
 * @version 1.0
 */

@Component
@RequiredArgsConstructor
public class TelegramBotConfig extends TelegramLongPollingBot {
    private final TelegramBotProperties botProperties;

    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;
    private final Map<Long, Boolean> uploadMode = new HashMap<>();

    /**
     * Возвращает имя пользователя Telegram-бота.
     *
     * @return имя пользователя, указанное в свойствах бота.
     */
    @Override
    public String getBotUsername() {
        return botProperties.getUsername();
    }

    /**
     * Возвращает токен Telegram-бота.
     *
     * @return токен, указанный в свойствах бота.
     */
    @Override
    public String getBotToken() {
        return botProperties.getToken();
    }

    /**
     * Основной метод обработки обновлений от Telegram.
     * <p>
     * Обрабатывает входящие сообщения и команды, делегируя логику
     * соответствующим методам и классам команд.
     * </p>
     *
     * @param update обновление, полученное от Telegram.
     */
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

    /**
     * Обрабатывает команды, отправленные пользователями.
     * <p>
     * Команды сопоставляются с зарегистрированными обработчиками в
     * виде {@code Map<String, BotCommand>}. Если команда не распознана,
     * бот отправляет сообщение об ошибке.
     * </p>
     *
     * @param update обновление, содержащее команду.
     * @param chatId идентификатор чата, из которого пришла команда.
     */
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

    /**
     * Обрабатывает загрузку документа, отправленного пользователем.
     * <p>
     * Если режим загрузки включён для текущего чата ({@code uploadMode}),
     * передаёт обновление на обработку в {@code UploadCommand}.
     * В противном случае отправляет сообщение о необходимости активировать
     * режим загрузки с помощью команды {@code /upload}.
     * </p>
     *
     * @param update обновление, содержащее документ.
     * @param chatId идентификатор чата, из которого пришло сообщение.
     */
    private void handleUploadCommand(Update update, Long chatId) throws Exception {
        if (uploadMode.getOrDefault(chatId, false)) {
            new UploadCommand(uploadMode, categoryService).handleDocument(update);
        } else {
            TelegramBotUtils.sendMessage(chatId, "Сначала используйте команду /upload, чтобы загрузить файл.");
        }
    }
}
