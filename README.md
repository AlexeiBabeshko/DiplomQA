# Документация
[План тестирования](https://github.com/AlexeiBabeshko/DiplomQA/blob/master/Documents/Plan.md)

[Отчет по итогам тестирования](https://github.com/AlexeiBabeshko/DiplomQA/blob/master/Documents/Report.md)

[Отчет по итогам автоматизации](https://github.com/AlexeiBabeshko/DiplomQA/blob/master/Documents/Summary.md)

# Запуск автотестов
## Установить приложения
1. Браузер: Chrome Версия 123.0.6312.60
2. Intelij IDEA
3. Docker Desktop

## Процедура запуска
1. Проверить, что порты 8080, 9999, 5432 или 3306 свободны (зависит от выбранной СУБД)
2. Склонировать репозиторий https://github.com/AlexeiBabeshko/DiplomQA
3. Открыть его в IDEA
4. Ввести в терминале команду `docker-compose up`
5. Для запуска с поддержкой
   - *СУБД MySQL*:
       - Ввести в терминале команду `java -jar ./artifacts/aqa-shop.jar`
       - Нажать `ctrl` дважды - открыть окно Run anything. Ввести команду `gradlew clean test`
   - *СУБД PostgreSQL*:
       - Ввести в терминале команду ` java "-Dspring.datasource.url=jdbc:postgresql://localhost:5432/app" -jar artifacts/aqa-shop.jar`
       - Нажать `ctrl` дважды - открыть окно Run anything. Ввести команду `gradlew clean test "-Ddb.url=jdbc:postgresql://localhost:5432/app"`
