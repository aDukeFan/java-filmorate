insert into ratings 
(name) values 
('G'),
('PG'),
('PG-13'),
('R'),
('NC-17');

insert into genres 
(name) values 
('Комедия'),
('Драма'),
('Мультфильм'),
('Триллер'),
('Документальный'),
('Боевик');

insert into users 
(name, email, login, birthday) values 
('U1', 'user1@ya.ru', 'user1', '2000-01-01'),
('U2', 'user2@ya.ru', 'user2', '2001-01-01'),
('U3', 'user3@ya.ru', 'user3', '2002-01-01'),
('U4', 'user4@ya.ru', 'user4', '2003-01-01'),
('U5', 'user5@ya.ru', 'user5', '2004-01-01');

insert into directors 
(name) values 
('Director 1'), 
('Director 2'), 
('Director 3');

insert into films 
(name, description, release, duration, rating_id) values 
('film1', 'gf', '2001-01-01', '150', '1'),
('film2', 'gf', '2002-01-01', '160', '2'),
('film3', 'gf', '2003-01-01', '170', '3'),
('film4', 'gf', '2004-01-01', '180', '4'),
('film5', 'gf', '2004-01-01', '180', '4');

insert into directors_films 
(film_id, director_id) values 
('1','1'),
('2','2'),
('3','3'),
('4','3'),
('5','3');

insert into genres_films 
(film_id, genre_id) values 
('1','3'),
('2','3'),
('3','3'),
('4','3'),
('5','3');
