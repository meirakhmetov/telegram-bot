# Telegram Bot Application

Это приложение Telegram-бота, разработанное на основе Spring Boot. Бот позволяет управлять категориями, загружать их из файлов Excel и просматривать структуру категорий.

## Содержание

- [Функциональность](#функциональность)
- [Технологии](#технологии)
- [Установка](#установка)
- [Настройка](#настройка)
- [Запуск приложения](#запуск-приложения)
- [Использование](#использование)

## Функциональность

- Добавление, удаление и просмотр категорий.
- Загрузка категорий из файлов Excel.
- Просмотр иерархической структуры категорий.
- Обработка команд от пользователей.

## Технологии

- Java 17
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Apache POI (для работы с Excel файлами)
- Telegram Bots Java API

## Установка

1. Склонируйте репозиторий:
   ```bash
   git clone https://github.com/meirakhmetov/telegram-bot.git
   cd telegram-bot
Убедитесь, что у вас установлены Java 23 и Maven.

Создайте базу данных PostgreSQL и настройте ее.

Добавьте зависимости проекта:

## Настройка
Создайте файл application.properties в директории src/main/resources и добавьте следующие параметры:

telegram.bot.username=Ваш_Юзернейм_Бота
telegram.bot.token=Ваш_Токен_Бота

spring.datasource.url=jdbc:postgresql://localhost:5432/ваша_база_данных

spring.datasource.username=ваш_пользователь

spring.datasource.password=ваш_пароль

spring.jpa.hibernate.ddl-auto=update


## Запуск приложения
Для запуска приложения используйте команду:

bash
mvn spring-boot:run

## Использование
Найдите вашего бота в Telegram и начните с ним диалог, набрав команды "/start".

Используйте команды:

/viewTree - Показать дерево категорий

/addElement <название элемента>

/addElement <родительский элемент>/<дочерний элемент>

/removeElement <родительский элемент>/<дочерний элемент>

/removeElement <название элемента>

/download - Скачать дерево категорий в формате Excel

/upload - Загрузить дерево категорий из Excel

