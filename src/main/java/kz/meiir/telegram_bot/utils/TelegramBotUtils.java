package kz.meiir.telegram_bot.utils;

import lombok.Setter;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.InputStream;
import java.io.IOException;
import java.net.URL;

/**
 * Утилитарный класс {@code TelegramBotUtils} предоставляет статические методы
 * для работы с Telegram Bot API.
 *
 * <h2>Описание:</h2>
 * Этот класс содержит методы для отправки сообщений и документов, а также
 * для получения входного потока файлов с использованием ID файла.
 *
 * <h2>Методы:</h2>
 * <ul>
 *     <li><strong>sendMessage</strong>: Отправляет текстовое сообщение в указанный чат.</li>
 *     <li><strong>getFileInputStream</strong>: Получает входной поток файла по его ID.</li>
 *     <li><strong>sendDocument</strong>: Отправляет документ в указанный чат.</li>
 * </ul>
 *
 * <h2>Использование:</h2>
 * Для использования методов данного класса необходимо установить экземпляр
 * {@link TelegramLongPollingBot} с помощью метода {@link #setBot(TelegramLongPollingBot)}.
 *
 * @author Meiir Akhmetov
 * @version 1.0
 */
public class TelegramBotUtils {

    @Setter
    private static TelegramLongPollingBot bot;

    /**
     * Отправляет текстовое сообщение в указанный чат.
     *
     * @param chatId ID чата, в который будет отправлено сообщение
     * @param text текст сообщения
     */
    public static void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            System.err.println("Ошибка отправки сообщения: " + e.getMessage());
        }
    }

    /**
     * Получает входной поток файла по его ID.
     *
     * @param fileId ID файла
     * @return входной поток для чтения файла
     * @throws IOException если произошла ошибка ввода-вывода
     * @throws TelegramApiException если произошла ошибка при вызове Telegram API
     */
    public static InputStream getFileInputStream(String fileId) throws IOException, TelegramApiException {
        GetFile getFile = new GetFile();
        getFile.setFileId(fileId);
        File file = bot.execute(getFile);
        return new URL(file.getFileUrl(bot.getBotToken())).openStream();
    }

    /**
     * Отправляет документ в указанный чат.
     *
     * @param chatId ID чата, в который будет отправлен документ
     * @param filePath путь к файлу на диске
     */
    public static void sendDocument(Long chatId, String filePath) {
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(chatId.toString());
        sendDocument.setDocument(new InputFile(new java.io.File(filePath)));

        try {
            bot.execute(sendDocument);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            System.err.println("Ошибка отправки документа: " + e.getMessage());
        }
    }
}
