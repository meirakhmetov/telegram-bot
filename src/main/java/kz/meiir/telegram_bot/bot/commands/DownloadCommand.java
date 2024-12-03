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

public class DownloadCommand implements BotCommand{
    private final CategoryRepository categoryRepository;

    public DownloadCommand(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void execute(Long chatId, String text) {
        String excelFilePath = createExcelFileWithCategoryTree();
        TelegramBotUtils.sendDocument(chatId, excelFilePath);
    }

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
