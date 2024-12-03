package kz.meiir.telegram_bot.bot.commands;

import kz.meiir.telegram_bot.service.CategoryService;
import kz.meiir.telegram_bot.utils.TelegramBotUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.InputStream;
import java.util.Map;

public class UploadCommand implements BotCommand {
    private final Map<Long, Boolean> uploadMode;

    public UploadCommand(Map<Long, Boolean> uploadMode) {
        this.uploadMode = uploadMode;
    }

    @Override
    public void execute(Long chatId, String command) {
        uploadMode.put(chatId, true);
        TelegramBotUtils.sendMessage(chatId, "Теперь вы можете загрузить файл Excel с деревом категорий.");
    }

    public void handleDocument(Update update, CategoryService categoryService) throws Exception {
        Long chatId = update.getMessage().getChatId();
        String fileId = update.getMessage().getDocument().getFileId();

        try (InputStream inputStream = TelegramBotUtils.getFileInputStream(fileId)) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                Cell categoryCell = row.getCell(0);
                Cell parentCell = row.getCell(1);

                if (categoryCell != null) {
                    String categoryName = categoryCell.getStringCellValue();
                    String parentName = parentCell != null ? parentCell.getStringCellValue() : null;

                    // Добавляем родительскую категорию, если её ещё нет
                    if (parentName != null && !parentName.isEmpty()) {
                        categoryService.addCategory(parentName, null);
                    }

                    // Добавляем категорию
                    categoryService.addCategory(categoryName, parentName);
                }
            }

            TelegramBotUtils.sendMessage(chatId, "Файл успешно загружен и обработан!");
        } catch (TelegramApiException e) {
            TelegramBotUtils.sendMessage(chatId, "Ошибка загрузки файла: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
