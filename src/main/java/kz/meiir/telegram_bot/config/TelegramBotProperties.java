package kz.meiir.telegram_bot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Класс {@code TelegramBotProperties} представляет собой конфигурацию для Telegram-бота.
 *
 * <h2>Описание:</h2>
 * Этот класс используется для загрузки свойств, связанных с конфигурацией Telegram-бота,
 * из файла настроек (например, application.properties или application.yml).
 *
 * <h2>Свойства:</h2>
 * <ul>
 *     <li><strong>username</strong>: Имя пользователя бота.</li>
 *     <li><strong>token</strong>: Токен доступа бота, необходимый для взаимодействия с API Telegram.</li>
 * </ul>
 *
 * <h2>Использование:</h2>
 * Экземпляр этого класса автоматически заполняется значениями, определенными в конфигурации Spring,
 * что позволяет удобно получать настройки бота в других компонентах приложения.
 *
 * @see <a href="https://core.telegram.org/bots/api#authorizing-your-bot">Telegram Bot API - Авторизация вашего бота</a>
 *
 * @author Meiir Akhmetov
 * @version 1.0
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "telegram.bot")
public class TelegramBotProperties {
    private String username;    // Имя пользователя бота
    private String token;       // Токен доступа бота
}
