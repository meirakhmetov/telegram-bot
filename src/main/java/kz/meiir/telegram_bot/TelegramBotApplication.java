package kz.meiir.telegram_bot;

import kz.meiir.telegram_bot.bot.TelegramBotConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.ApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
/**
 * Главный метод приложения.
 *
 * <p>Метод выполняет следующие действия:
 * <ul>
 *     <li>Запускает Spring Boot приложение и инициализирует контекст приложения.</li>
 *     <li>Создаёт объект {@link TelegramBotsApi} для работы с Telegram API.</li>
 *     <li>Регистрирует бота с помощью конфигурации {@link TelegramBotConfig}.</li>
 * </ul>
 * </p>
 *
 * @param args аргументы командной строки.
 */
@SpringBootApplication
public class TelegramBotApplication {

	// Инициализация Spring контекста
	public static void main(String[] args) {
		ApplicationContext applicationContext= SpringApplication.run(TelegramBotApplication.class, args);

		try {
			// Создание API для работы с Telegram Bots
			TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
			// Регистрация бота
			botsApi.registerBot(applicationContext.getBean(TelegramBotConfig.class));
		} catch (TelegramApiException e) {
			// Обработка исключений при регистрации бота
			e.printStackTrace();
		}
	}

}
