package kz.meiir.telegram_bot;

import kz.meiir.telegram_bot.bot.TelegramBotConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.ApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class TelegramBotApplication {

	public static void main(String[] args) {
		ApplicationContext applicationContext= SpringApplication.run(TelegramBotApplication.class, args);

		try {
			TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
			botsApi.registerBot(applicationContext.getBean(TelegramBotConfig.class));
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

}
