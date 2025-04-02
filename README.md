## Weather Visualization

### О проекте

Этот проект создан в микросервисной архитектуре, backend реализован на Java Spring Boot, frоntend на React. Проект 
разворачивается в контейнере. Основная цель – получить практический опыт в построении веб-приложения, где фронтенд и 
бэкенд работают независимо и взаимодействуют через API.

Приложение визуализирует погодные данные. Данные из статичного datset-а представленны в PostgreSQL, визуализируются на 
стороне backend-а и передаются в формате SVG. Tакже есть модуль генерирующий данные в реальном времени, они рендарятся на стороне клиента из json объекта.

Сервисы backend-а скрыты внутри Docker-сети, взаимодействие с ним происходит через единую точку входа Authю. Модуль валидирует JWT токены и предоставляет прокси для доступа к другим 
модулям.

В проекте используется JWT-аутентификация. Фронтенд получает токен при входе пользователя и отправляет его в заголовках 
при каждом запросе к API. CORS-политика настроена так, чтобы взаимодействие происходило только с доверенных доменов.


### Освоенные навыки

В процессе работы над проектом были изучены и применены следующие технологии:

- Spring Boot – построение REST API, работа с базой данных и реализация аутентификации.
- JWT-токены – защита API и управление доступом.
- PostgreSQL – хранение данных с изоляцией внутри Docker-сети. Подъем базы SQL сриптом.
- React – рендеринг данных, работа с состоянием и обновление интерфейса на основе API.
- Docker – контейнеризация приложения и изоляция сервисов.
- Микросервисная архитектура – разделение бэкенда на независимые сервисы, которые общаются через API.
- CORS-политика – контроль доступа к ресурсам сервера.

### Как все устроено?

В корне проекта находятся две основные директории:

**/Frontend** – React-приложение, которое получает данные из API и отображает их в виде графиков.

**/Backend** – микросервисный бэкенд на Java Spring Boot, состоящий из нескольких модулей:

- Auth – сервис аутентификации, который выдает JWT-токены и проксирует запросы внутри сети.
- Generator – сервис, предоставляющий статические данные для визуализации.
- RealtimeWeatherSimulator – сервис, генерирующий случайные погодные данные в реальном времени и хранящий их за последние 3 минуты.
- PostgreSQL – база данных, работающая без внешнего порта, доступная только внутри контейнерной сети.




Запуск всех компонентов выполняется через docker-compose.yml, который поднимает всю инфраструктуру:

![Архитектура](https://github.com/lectorem-dev/WeatherAnalyzer/blob/main/Dataset/Architecture.png)

### Запуск 

Для запуска приложения необходимо иметь Docker и Maven (Для билда jar из исходного кода).

1. Клонировать репозиторий:

``` bash
git clone https://github.com/lectorem-dev/WeatherAnalyzer.git
```

* Можно открыть папку WeatherAnalyzer и воспользоваться start-prod.bat чтобы запустить проект, а потом 
очистить докер с помощью cleanup-docker.bat

2. Перейти в папку проекта:
```bash
cd WeatherAnalyzer
```

3. Собрать jar

```bash
mvn package
```

4. Запустить Docker

```bash
docker-compose up --build
```

5. Открыть в браузере:

Фронтенд : [React](http://localhost:3000) - *login:* ivanovii *password:* qwerty123

Документация эндпоинтов backend-a (Swagger):

- [Generator API](http://localhost:8000/sw/generator/swagger-ui/index.html)
- [Simulator API](http://localhost:8000/sw/simulator/swagger-ui/index.html)
