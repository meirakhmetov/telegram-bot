package kz.meiir.telegram_bot.bot.commands;

import kz.meiir.telegram_bot.model.Category;
import kz.meiir.telegram_bot.repository.CategoryRepository;
import kz.meiir.telegram_bot.service.CategoryService;
import kz.meiir.telegram_bot.utils.TelegramBotUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Команда {@code DownloadCommand} предназначена для генерации и отправки Excel-файла
 * с деревом категорий в чат Telegram.
 *
 * <h2>Описание:</h2>
 * Этот класс создает Excel-файл, содержащий категории и их родительские категории.
 * Затем файл отправляется в чат пользователя.
 *
 * <h2>Формат Excel-файла:</h2>
 * <ul>
 *     <li>Столбец 1: Название категории</li>
 *     <li>Столбец 2: Полный путь родительских категорий (разделённый через " / ")</li>
 * </ul>
 *
 * <h2>Использование:</h2>
 * Этот класс используется, когда пользователь отправляет команду <code>/download</code>.
 *
 * <h2>Пример вызова:</h2>
 * <pre>
 * new DownloadCommand(categoryRepository).execute(chatId, "/download");
 * </pre>
 *
 * @author Meiir Akhmetov
 * @version 1.0
 */

public class DownloadCommand implements BotCommand{
    private final CategoryRepository categoryRepository;
    /**
     * Конструктор для инициализации {@code DownloadCommand}.
     *
     * @param categoryRepository репозиторий категорий для получения данных.
     */
    public DownloadCommand(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Выполняет команду загрузки дерева категорий и отправляет Excel-файл в чат.
     *
     * @param chatId идентификатор чата, куда будет отправлен файл.
     * @param text   текст команды (не используется в данной реализации).
     */
    @Override
    public void execute(Long chatId, String text) {
        String excelFilePath = createExcelFileWithCategoryTree();
        TelegramBotUtils.sendDocument(chatId, excelFilePath);
    }

    /**
     * Генерирует Excel-файл с деревом категорий.
     *
     * <h2>Структура данных:</h2>
     * <ul>
     *     <li>Категории и их родительские элементы извлекаются из репозитория.</li>
     *     <li>Родительские категории формируются в виде строки, разделённой " / ".</li>
     * </ul>
     *
     * @return путь к созданному Excel-файлу.
     */
    private String createExcelFileWithCategoryTree() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Категории");

        // Добавление заголовков
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Категория");
        header.createCell(1).setCellValue("Родительская категория");

        // Получение дерева категорий
        List<Category> categories = categoryRepository.findAll();
        int rowNum = 1;
        for (Category category : categories) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(category.getName());

            // Сбор всех родительских категорий
            if (category.getParent() != null) {
                StringBuilder parentNames = new StringBuilder();
                Category parent = category.getParent();
                while (parent != null) {
                    parentNames.insert(0, parent.getName() + " / "); // Вставляем перед текущим
                    parent = parent.getParent(); // Переходим к следующему уровню родителя
                }
                row.createCell(1).setCellValue(parentNames.toString()); // Указываем все родительские категории
            } else {
                row.createCell(1).setCellValue(""); // Если родителя нет
            }
        }

        String filePath = "categories_tree.xlsx"; // Путь к файлу
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return filePath; // Возвращаем путь к созданному файлу
    }
}
