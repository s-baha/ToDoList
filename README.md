Task Manager App

Приложение для управления задачами с возможностью регистрации, авторизации и работы со списком задач, реализованное с использованием Jetpack Compose.

Технологии

1)Jetpack Compose — декларативный подход к созданию UI

2)MVVM (Model-View-ViewModel) — архитектурный шаблон

3)Navigation Compose — навигация между экранами

4)Kotlin DSL — конфигурация Gradle

5)Kotlin — основной язык программирования

Структура проекта
app/

 └── src
 
      ├── main/
      │   ├── java/com/example/project/
      │   │   ├── ui/screens/auth/         // Экраны авторизации и регистрации
      │   │   ├── ui/screens/main/         // Экран со списком задач
      │   │   ├── ui/components/           // Компоненты пользовательского интерфейса
      │   │   ├── data/                    // Сущности и репозитории
      │   │   ├── navigation/              // Логика навигации
      │   │   └── App.kt, MainActivity.kt  // Точка входа

      
Как запустить
Клонируйте репозиторий:
git clone https://github.com/s-baha/ToDoList
cd task-manager-app

Откройте проект в Android Studio.

Убедитесь, что установлены:

  Android SDK (API 33+)
  
  Kotlin 1.9+
  
  Поддержка Jetpack Compose
  
Нажмите "Run" для запуска приложения на эмуляторе или устройстве.



