# Проект "java-kanban"
ShareIt - cервис трекера задач.

Основные возможности
Сервис обеспечивает пользователям возможность:

* Выводить список в порядке приоритета;
* Создавать задачи первой, второй и третьей степени важности и могут быть составными;
* Просматривать историю выполнения задач. 

Есть проверка задач и подзадач на пересечение по времени выполнения. Данные могут
хранится в файлах или в хранилище на сервере.

Этапы жизни задачи:
* Новая
* В процессе
* Выполнена
  
Методы для каждого из типа задач:
* Получение списка всех задач
* Удаление всех задач
* Получение по идентификатору
* Создание.
* Обновление.
* Удаление по идентификатору.

Технологии
* Java 11;
* фреймворк JUnit;

Инструкция по развертыванию:
* Скачать данный репозиторий
* Выполнить команду mvn clean install
* Запустить Jar-файл java -jar filename.jar, где filename - название исполняемого файла
