# java-filmorate 
## Cоциальная сеть для киноманов
#### [cсылка](https://disk.yandex.ru/i/59G1473itKsYiw) на схему базы данных
### Примеры запросов sql:
* #### добавить рейтинги:
````
insert into ratings (name)
values('PG-13');
```` 
* #### добавить новый фильм
````
insert into films (name, description, release, duration, rating_id)
values('Пираты карибского моря', 'проклятье черной жемчужины', '2003-06-28', '143', '2');
````
* #### обновить существующий фильм
````
update films set release = '2009-07-01'
where id = ?;
````
* #### получить фильм по id
````
select * 
from films 
where id = ?;
````
* ####  получить все фильмы
````
select * 
from films;
````
* ####  поставить фильму лайк
````
insert into likes (id_film, id_user)
values (?, ?);
````
* ####  удалить лайк лайк у фильма
````
delete from likes 
where id_film = ? and id_user = ?;
````
* ####  получить топ популярных фильмов
````
select * 
from films 
where id in (select id_film from likes
                    group by id_film 
                    order by count(id_user) desc);
````
* ####  добавить жанр
````
insert into genres (name)
values('comedy');
````
* ####  задать фильму жанр
````
insert into films_genres (id_film, id_genre) 
values(?, ?);
````
* ####  получить жанр жанр по id
````
select * 
from genres 
where id = ?;
````
* ####  получить список жанров
````
select * 
from genres;
````
* ####  получить рейтинг по id
````
select * 
from ratings
where id = ?;
````
* ####  получить список рейтингов
````
select * 
from ratings;
````
* ####  добавление нового пользователя
````
insert into users (name, login, email, birthday)
values('Виктор Цой', 'kino', 'kino@ya.ru', '1962-06-21');
````
* ####  обновление существующего пользователя
````
update users set name = 'Виктор Робертович Цой'
where id = ?;
````
* ####  получение пользователя по id
````
select *
from users
where id = ?;
````
* ####  получение списка всех пользователей
````
select * 
from users;
````
* ####  добавить пользователя в друзья = подписаться
````
insert into follows (id_following_user, id_followed_user) 
values(?, ?);
````
* ####  отписаться
````
delete from follows
where id_following_user = ? and id_followed_user = ?;
````
* ####  показать список друзей = подписчиков
````
select * from users 
where id in (select id_following_user
             from follows
             where id_followed_user = ?);
````
* ####  показать общих друзей = общих подписчиков
````
select *
from users 
where id in (select id_following_user
             from follows
             where id_followed_user = ?) 
             and id in (select id_following_user
                        from follows
                        where id_followed_user = ?);
````