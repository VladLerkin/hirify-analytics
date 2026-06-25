# Hirify Analytics - Web Version

Веб-версия приложения Hirify Analytics, созданная с использованием Kotlin/Wasm и Compose Multiplatform.

## Требования

- JDK 25 или выше
- Node.js и npm (устанавливаются автоматически через Gradle)

## Сборка

### Production сборка

Для создания production сборки выполните:

```bash
./gradlew :app-web:wasmJsBrowserDistribution
```

Результат будет находиться в `app-web/build/dist/wasmJs/productionExecutable/`

### Development сборка с автоматической перезагрузкой

Для запуска dev-сервера с hot reload:

```bash
./gradlew :app-web:wasmJsBrowserDevelopmentRun
```

Приложение будет доступно по адресу: http://localhost:8080

## Запуск собранной версии

После сборки production версии, вы можете запустить веб-сервер в директории с результатами:

```bash
cd app-web/build/dist/wasmJs/productionExecutable/
python3 -m http.server 8000
```

Затем откройте в браузере: http://localhost:8000

## Структура проекта

```
app-web/
├── build.gradle.kts              # Конфигурация сборки
├── src/
│   └── wasmJsMain/
│       ├── kotlin/
│       │   └── hirify/analytics/ui/
│       │       └── Main.kt       # Точка входа приложения
│       └── resources/
│           └── index.html        # HTML страница
└── README.md
```

## Особенности веб-версии

### Реализованные функции
- Основной UI приложения (аналитика и дашборды)
- Графики и таблицы данных
- Настройка параметров и фильтров

### Ограничения
- **Хранилище настроек**: localStorage API пока не подключен (в разработке)
- Не все системные диалоги могут работать из браузера.

### Технические детали
- **Kotlin**: 2.4.0
- **Compose Multiplatform**: 1.11.1
- **Vite**: 5.4.11
- **Gradle**: 9.5.1 (с поддержкой wasmJs)
- **Ktor Client**: 3.5.0 (с поддержкой wasmJs)
- **Target**: wasmJs (WebAssembly для JavaScript)

## Разработка

При разработке веб-версии учитывайте:

1. Все платформенные реализации находятся в `wasmJsMain` source set
2. Для доступа к Web APIs используйте `kotlinx.browser`
3. Некоторые функции могут быть недоступны в браузере (например, системные диалоги)

## Отладка

Для отладки в браузере:
1. Откройте DevTools (F12)
2. Перейдите во вкладку Console для просмотра логов
3. Source maps включены в development сборке

## Производительность

Production сборка оптимизирована с помощью:
- Webpack минификация
- Binaryen оптимизация WASM
- Code splitting (где возможно)

## Поддержка браузеров

Требуется современный браузер с поддержкой:
- WebAssembly
- ES6 modules
- Canvas API (для Compose rendering)

Рекомендуемые браузеры:
- Chrome/Edge 119+
- Firefox 120+
- Safari 17+

## Лицензия

Та же, что и основной проект Hirify Analytics.
