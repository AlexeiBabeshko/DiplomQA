# Отчет по итогам тестирования

## Описание
Проведено автоматизированное тестирование функциональности "покупка тура", в соответствии с [планом тестирования](https://github.com/AlexeiBabeshko/DiplomQA/blob/master/Documents/Plan.md).

**Общее количество тестов:** 61.

## Итоги тестирования
### При подключении к СУБД MySQL
  - Passed test 77%
  - Failed test 23%
    
![image](https://github.com/AlexeiBabeshko/DiplomQA/assets/155017939/65b4260b-9ce7-4515-80dd-146fe42d3cec)

![image](https://github.com/AlexeiBabeshko/DiplomQA/assets/155017939/8832b9d2-5dd3-4284-a43f-3a5a07378770)
![image](https://github.com/AlexeiBabeshko/DiplomQA/assets/155017939/e067fd26-0fb0-4093-b10d-63329d7c7642)
![image](https://github.com/AlexeiBabeshko/DiplomQA/assets/155017939/d0d6bc69-3ff7-4f73-9a44-ee1afdded011)

### При подключении к СУБД PostgreSQL
  - Passed test 77%
  - Failed test 23%

![image](https://github.com/AlexeiBabeshko/DiplomQA/assets/155017939/917390d3-f299-4dcc-916f-2bd43e58a65f)

![image](https://github.com/AlexeiBabeshko/DiplomQA/assets/155017939/fbcd892d-50eb-4f21-82a9-018071fcdf56)
![image](https://github.com/AlexeiBabeshko/DiplomQA/assets/155017939/5480cc90-b81f-411c-b03e-cee597ff179a)
![image](https://github.com/AlexeiBabeshko/DiplomQA/assets/155017939/b13e74dd-8c13-4107-8199-cacf95d1ad85)

На найденные дефекты заведены [баг-репорты](https://github.com/AlexeiBabeshko/DiplomQA/issues)

## Рекомендации

1. Для большей стабильности тестов добавить тестовые метки к элементам страницы
2. Выбрать 1 СУБД для работы, использование 2 СУБД ничем не оправдано.
3. Исправить найденные дефекты
