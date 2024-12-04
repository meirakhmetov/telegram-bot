package kz.meiir.telegram_bot.bot.commands;

import kz.meiir.telegram_bot.service.CategoryService;
import kz.meiir.telegram_bot.utils.TelegramBotUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
/**
 * Команда {@code UploadCommand} используется для загрузки файла Excel с деревом категорий.
 *
 * <h2>Описание:</h2>
 * Этот класс активирует режим загрузки для пользователя, позволяет загружать документ,
 * обрабатывает его содержимое и добавляет категории в базу данных.
 *
 * <h2>Использование:</h2>
 * <ul>
 *     <li>Выполнение команды <code>/upload</code> активирует режим загрузки.</li>
 *     <li>Пользователь может загрузить файл Excel с двумя колонками: название категории и родительская категория.</li>
 * </ul>
 *
 * @author Meiir Akhmetov
 * @version 1.0
 */
public class UploadCommand implements BotCommand {
    private final Map<Long, Boolean> uploadMode;
    private final CategoryService categoryService;

    /**
     * Конструктор для создания экземпляра {@code UploadCommand}.
     *
     * @param uploadMode   Состояние режима загрузки для пользователей (по chatId).
     * @param categoryService Сервис для работы с категориями.
     */
    public UploadCommand(Map<Long, Boolean> uploadMode, CategoryService categoryService) {
        this.uploadMode = uploadMode;
        this.categoryService = categoryService;
    }

    /**
     * Активирует режим загрузки для указанного пользователя.
     *
     * @param chatId  идентификатор чата, откуда пришла команда.
     * @param command текст команды (например, "/upload").
     */
    @Override
    public void execute(Long chatId, String command) {
        uploadMode.put(chatId, true);
        TelegramBotUtils.sendMessage(chatId, "Теперь вы можете загрузить файл Excel с деревом категорий.");
    }

    /**
     * Обрабатывает загруженный файл Excel и добавляет категории в базу данных.
     *
     * @param update объект {@link Update}, содержащий данные о загруженном документе.
     * @throws Exception если произошла ошибка обработки файла.
     */
    public void handleDocument(Update update) throws Exception {
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

            downloadAndProcessFile(fileId);
            TelegramBotUtils.sendMessage(chatId, "Файл успешно загружен и обработан!");
        } catch (TelegramApiException e) {
            TelegramBotUtils.sendMessage(chatId, "Ошибка загрузки файла: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            uploadMode.put(chatId, false);
        }
    }

    /**
     * Загружает и обрабатывает файл Excel, добавляя категории в базу данных.
     *
     * @param fileId идентификатор файла, загруженного в Telegram.
     * @throws IOException если произошла ошибка при чтении файла.
     */
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
