package kz.meiir.telegram_bot.utils;

import kz.meiir.telegram_bot.config.TelegramBotProperties;
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

public class TelegramBotUtils {

    private static TelegramLongPollingBot bot;

    public static void setBot(TelegramLongPollingBot telegramBot) {
        bot = telegramBot;
    }

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

    public static InputStream getFileInputStream(String fileId) throws IOException, TelegramApiException {
        GetFile getFile = new GetFile();
        getFile.setFileId(fileId);
        File file = bot.execute(getFile);
        return new URL(file.getFileUrl(bot.getBotToken())).openStream();
    }

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
