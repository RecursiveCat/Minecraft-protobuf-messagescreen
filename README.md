# Fabric Protobuf Demo

**Тестовое задание:** разработать мод под **Minecraft 1.21.8** (Fabric API) с отправкой сообщений от клиента к серверу через **Protobuf** и сохранением их в **PostgreSQL** с помощью **Hibernate**.

---


1. **Клиентская часть**
   - Отображает простой экран (**Screen**) с полем ввода и кнопкой.
   - При нажатии на кнопку создаётся и отправляется **Protobuf-сообщение**:
     ```proto
     message Message {
       string text = 1;
     }
     ```
   - Сообщение сериализуется в бинарный формат и передаётся серверу через Fabric networking API.

2. **Серверная часть**
   - Принимает Protobuf-пакет от клиента.
   - Десериализует сообщение и сохраняет его в PostgreSQL.
   - Таблица БД:
     ```sql
     CREATE TABLE messages (
         id SERIAL PRIMARY KEY,
         uuid UUID NOT NULL,
         text VARCHAR(256) NOT NULL
     );
     ```

3. **Инфраструктура**
   - ORM — Hibernate + JPA.
   - Используются официальные Mojang-мэппинги.

---

## Инструкция по запуска
1. Склонируйте проект
```
git clone https://github.com/RecursiveCat/Minecraft-protobuf-messagescreen.git
cd Minecraft-protobuf-messagescreen
```
2. Запустите докер-контейнер с Postgres
```
docker-compose up -d
```
3. Соберите и запустите клиент
```
./gradlew runClient --args="--offline"
```
3. Запустите сервер
```
./gradlew runServer
```
