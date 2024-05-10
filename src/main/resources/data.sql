
insert into ratings (name) values ('G');
insert into ratings (name) values ('PG');
insert into ratings (name) values ('PG-13');
insert into ratings (name) values ('R');
insert into ratings (name) values ('NC-17');

insert into genres (name) values ('Комедия');
insert into genres (name) values ('Драма');
insert into genres (name) values ('Мультфильм');
insert into genres (name) values ('Триллер');
insert into genres (name) values ('Документальный');
insert into genres (name) values ('Боевик');

insert into entities(name) values ('user');
insert into entities(name) values ('film');
insert into entities(name) values ('review');

insert into entities(name) values ('user');
insert into entities(name) values ('film');

insert into events(name) values ('LIKE');
insert into events(name) values ('REVIEW');
insert into events(name) values ('FRIEND');

insert into operations(name) values ('REMOVE');
insert into operations(name) values ('ADD');
insert into operations(name) values ('UPDATE');