package kz.meiir.telegram_bot.bot;

import kz.meiir.telegram_bot.model.Category;
import kz.meiir.telegram_bot.repository.CategoryRepository;
import kz.meiir.telegram_bot.service.CategoryService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TelegramBotConfig extends TelegramLongPollingBot {
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Getter
    @Value("${telegram.bot.token}")
    private String botToken;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Long chatId = update.getMessage().getChatId();

            if (update.getMessage().isCommand()) {
                String command = update.getMessage().getText();

                switch (command) {
                    case "/start":
                        sendMessage(chatId, "Добро пожаловать! Введите /help для списка команд.");
                        break;

                    case "/viewTree":
                        String tree = categoryService.getCategoryTree();
                        sendMessage(chatId, tree != null ? tree : "Дерево категорий пусто.");
                        break;

                    case "/help":
                        sendMessage(chatId, "/viewTree - Показать дерево категорий\n" +
                                "/addElement <название> - Добавить элемент\n" +
                                "/removeElement <название> - Удалить элемент\n" +
                                "/download - Скачать дерево категорий в формате Excel\n" +
                                "/upload - Загрузить дерево категорий из Excel");
                        break;

                    case "/download":
                        String excelFilePath = createExcelFileWithCategoryTree();
                        sendDocument(chatId, excelFilePath);
                        break;

                    default:
                        handleAddElementCommand(command, chatId);
                        handleRemoveElementCommand(command, chatId);
                        break;
                }
            } else if (update.getMessage().hasDocument()) {
                handleUploadCommand(update);
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
                    parentNames.insert(0, parent.getName() + " "); // Вставляем перед текущим
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

    private void handleAddElementCommand(String command, Long chatId) {
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

    private void handleRemoveElementCommand(String command, Long chatId) {
        if (command.startsWith("/removeElement")) {
            String[] parts = command.split(" ", 2);
            if (parts.length == 2) {
                String elementName = parts[1];
                String response = categoryService.removeCategory(elementName);
                sendMessage(chatId, response);
            } else {
                sendMessage(chatId, "Неверный формат команды. Используйте:\n" +
                        "/removeElement <название элемента>");
            }
        }
    }

    private void handleUploadCommand(Update update) {
        String fileId = update.getMessage().getDocument().getFileId();
        Long chatId = update.getMessage().getChatId();

        try {
            downloadAndProcessFile(fileId);
            sendMessage(chatId, "Файл успешно загружен и обработан!");
        } catch (Exception e) {
            sendMessage(chatId, "Ошибка при загрузке файла: " + e.getMessage());
        }
    }

    private void downloadAndProcessFile(String fileId) throws IOException {
        try (InputStream inputStream = getFileInputStream(fileId)) {
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



    // Метод для получения InputStream файла
    private InputStream getFileInputStream(String fileId) throws IOException, TelegramApiException {
        GetFile getFile = new GetFile();
        getFile.setFileId(fileId);
        File file = execute(getFile);
        return new URL(file.getFileUrl(getBotToken())).openStream();
    }

    // Метод для отправки документа
    private void sendDocument(Long chatId, String filePath) {
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(chatId.toString());
        sendDocument.setDocument(new InputFile(new java.io.File(filePath))); // Корректное создание InputFile

        try {
            execute(sendDocument);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            System.err.println("Ошибка отправки документа: " + e.getMessage());
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
