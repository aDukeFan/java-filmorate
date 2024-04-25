# java-filmorate 
## Cоциальная сеть для киноманов
![TablesDiagramDB](https://github.com/okidokiluckyloki/java-filmorate/assets/148979016/6433e8f2-f06a-43fe-a1e8-67ac6537ce3b)
### Схема SQL таблиц: [Schema](https://github.com/okidokiluckyloki/java-filmorate/blob/add-database/src/main/resources/schema.sql)
### Примеры запросов sql:
* ####  Сохранить значения полей объекта в таблицу "example"
```sqlite-psql
INSERT INTO example (column_1, column_2, column_3, etc) 
VALUES(?, ?, ?, ?)
````
* ####  Получить id последнего сохраненного объекта в таблице, где id generated by default as identity not null primary key
```sqlite-psql
SELECT MAX(id) AS last 
FROM example
````
* ####  Получить поле из таблицы example по id
```sqlite-psql
SELECT column_name
FROM example
WHERE id = ?
````
* ####  Обновить рейтинг фильму
```sqlite-psql
UPDATE films 
SET rating_id = ?
WHERE id = ?
````
* ####  Получить жанр по id
```sqlite-psql
SELECT name 
FROM genres 
WHERE id = ?
````
* ####  Добавить фильму жанры
```sqlite-psql
INSERT INTO genres_films (genre_id, film_id)
VALUES(?, ?)
````
* ####  Обновить фильм без рейтинга
```sqlite-psql
UPDATE films 
SET name = ?,
    description = ?,
    release = ?,
    duration = ? 
WHERE id = ?
````
* ####  Обновить фильму рейтинг
```sqlite-psql
UPDATE films 
SET rating_id = ?
WHERE id = ?
````
* ####  Удалить все жанры конкретного фильма
```sqlite-psql
DELETE 
FROM genres_films
WHERE film_id = ?
````
* ####  Получить фильм без жанра
```sqlite-psql
SELECT name,
       description,
       release,
       duration 
FROM films 
WHERE id = ?
````
* ####  Получить id рейтинга фильма
```sqlite-psql
SELECT rating_id AS mpa_id
FROM films
WHERE id = ?
````
* ####  Получить рейтинг по id
```sqlite-psql
SELECT * 
FROM ratings
WHERE id = ?
````
* ####  Получить id жанров фильма
```sqlite-psql
SELECT genre_id AS id
FROM genres_films 
WHERE film_id = ?
````
* ####  Получить жанр по id
```sqlite-psql
SELECT * 
FROM genres
WHERE id = ?
````
* ####  Получить список лайков
```sqlite-psql
SELECT user_id AS id 
FROM likes 
WHERE film_id = ?
````
* ####  Получить id фильмов по порядку
```sqlite-psql
SELECT id 
FROM films 
ORDER BY id ASC
````
* ####  Поставить фильму лайк
```sqlite-psql
INSERT INTO likes (film_id, user_id)
VALUES(?, ?)
````
* ####  Удалить лайк у фильма
```sqlite-psql
DELETE 
FROM likes 
WHERE film_id = ? 
  AND user_id = ?
````
* ####  Получить топ 10 id фильмов по лайкам
```sqlite-psql
SELECT film_id AS id 
FROM likes
GROUP BY film_id
ORDER BY COUNT(user_id) DESC
LIMIT 10
````
* ####  Узнать получил ли фильм лайк от конкретного пользователя
```sqlite-psql
SELECT EXISTS (SELECT * 
               FROM likes
               WHERE film_id = ? 
                 AND user_id = ?) AS match
````
* ####  Узнать задан ли фильму сохраненному в БД рейтинг
```sqlite-psql
SELECT EXISTS (SELECT rating_id
               FROM films
               WHERE rating_id IS NOT NULL 
                 AND id = ?) AS match
````
* ####  Узнать есть ли запрашиваемый id в таблице "tableName"
```sqlite-psql
SELECT EXISTS (SELECT id AS match 
               FROM tableName
               WHERE id = ?) AS match
````
* ####  Обновить пользователя
```sqlite-psql
UPDATE users 
SET name = ?, login = ?, email = ?, birthday = ?
WHERE id = ?
````
* ####  получить подписчиков пользователя
```sqlite-psql
SELECT following_id 
FROM follows 
WHERE followed_id = ?
````
* ####  получить пользователя по id без подписчиков
```sqlite-psql
SELECT * 
FROM users 
WHERE id = ?
````
* ####  получить всех пользователей по порядку без подписчиков
```sqlite-psql
SELECT * 
FROM users 
ORDER BY id ASC"
````
* ####  подписаться
```sqlite-psql
INSERT INTO follows (following_id, followed_id) 
VALUES(?, ?)
````
* ####  отписаться
```sqlite-psql
DELETE 
FROM follows 
WHERE following_id = ? 
  AND followed_id = ?
````
* ####  получить общий подписчиков без их подписчиков (они же друзья)
```sqlite-psql
SELECT * 
FROM users AS u
JOIN follows AS f ON f.following_id = u.id 
                 AND f.followed_id = ?
JOIN follows AS friend_f ON friend_f.following_id = u.id 
                        AND friend_f.followed_id = ?"
````
* ####  получить всех подписчиков пользователя по id (без подписок подписчиков
```sqlite-psql
SELECT *  
FROM users u 
JOIN follows AS f ON f.following_id = u.id 
                 AND f.followed_id = ?;
````
* ####  получить id подписчиков пользователя
```sqlite-psql
SELECT following_id 
FROM follows 
WHERE followed_id = ?
````
* ####  получить все жанры по порядку id
```sqlite-psql
SELECT * 
FROM genres 
ORDER BY id ASC
````
* ####  получить жанр по id
```sqlite-psql
SELECT * 
FROM genres 
WHERE id = ?
````
* ####  получить все рейтинги по порядку id
```sqlite-psql
SELECT * 
FROM genres 
ORDER BY id ASC
````
* ####  получить рейтинг по id
```sqlite-psql
SELECT * 
FROM ratings
WHERE id = ?
````
#### проверил: [студент](https://github.com/awdiru)
