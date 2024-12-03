package kz.meiir.telegram_bot.bot;

import kz.meiir.telegram_bot.bot.commands.*;
import kz.meiir.telegram_bot.config.TelegramBotProperties;
import kz.meiir.telegram_bot.repository.CategoryRepository;
import kz.meiir.telegram_bot.service.CategoryService;
import kz.meiir.telegram_bot.utils.TelegramBotUtils;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.InputStream;
import java.io.IOException;
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
        // Установите экземпляр бота в утилитах
        TelegramBotUtils.setBot(this);

        if (update.hasMessage()) {
            Long chatId = update.getMessage().getChatId();

            if (update.getMessage().isCommand()) {
                String command = update.getMessage().getText();
                switch (command) {
                    case "/start":
                        new StartCommand().execute(chatId,command);
                        break;

                    case "/viewTree":
                        new ViewTreeCommand(categoryService,categoryRepository).execute(chatId,command);

                        break;

                    case "/help":
                        new HelpCommand().execute(chatId,command);
                        break;

                    case "/download":
                        new DownloadCommand(categoryRepository).execute(chatId,command);
                        break;

                    case "/upload":
                        new UploadCommand(uploadMode).execute(chatId,command);
                        break;

                    default:
                        new AddElementCommand(categoryService).execute(chatId,command);
                        new RemoveElementCommand(categoryService).execute(chatId,command);
                        break;
                }
            } else if (update.getMessage().hasDocument()) {
                if (uploadMode.getOrDefault(chatId, false)) {
                    handleUploadCommand(update);
                    uploadMode.put(chatId, false);
                } else {
                    TelegramBotUtils.sendMessage(chatId, "Сначала используйте команду /upload, чтобы загрузить файл.");
                }
            }
        }
    }

    private void handleUploadCommand(Update update) {
        String fileId = update.getMessage().getDocument().getFileId();
        Long chatId = update.getMessage().getChatId();

        try {
            downloadAndProcessFile(fileId);
            TelegramBotUtils.sendMessage(chatId, "Файл успешно загружен и обработан!");
        } catch (Exception e) {
            TelegramBotUtils.sendMessage(chatId, "Ошибка при загрузке файла: " + e.getMessage());
        }
    }

    private void downloadAndProcessFile(String fileId) throws IOException {
        try (InputStream inputStream = TelegramBotUtils.getFileInputStream(fileId)) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                Cell categoryCell = row.getCell(0);
                Cell parentCell = row.getCell(1);

                if (categoryCell != null) {
                    String categoryName = categoryCell.getStringCellValue();
                    String parentName = parentCell != null ? parentCell.getStringCellValue() : null;

                    // Если родительское имя указано, сначала добавляем родительскую категорию, если она отсутствует
                    if (parentName != null && !parentName.isEmpty()) {
                        categoryService.addCategory(parentName, null); // Сначала добавляем родителя
                    }
                    categoryService.addCategory(categoryName, parentName); // Добавление категории с родителем
                }
            }
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
