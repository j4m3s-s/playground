create schema if not exists api;

-- models
drop table api.recipes cascade;
create table api.recipes (
	id serial primary key,
	name text,
	search_name tsvector,
	cook_time interval,
	prep_time interval,
	perform_time interval,
	total_time interval,
	created date,
	modified date
);

insert into api.recipes
(name, search_name, created, modified)
values
('Patato potato', 'Patato potato', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


drop table api.ingredients cascade;
create table api.ingredients (
	id serial primary key,
	name text,
	search_name tsvector
);
insert into api.ingredients
(name, search_name)
values
('Potato', 'Potato');
insert into api.ingredients
(name, search_name)
values
('carotte', 'carotte');


drop table api.ingredients_recipe cascade;
create table api.ingredients_recipe (
	recipe_id serial references api.recipes(id),
	ingredient_id serial references api.ingredients(id)
);
insert into api.ingredients_recipe
(select api.recipes.id, api.ingredients.id from api.recipes, api.ingredients where api.recipes.name = 'Patato potato' and api.ingredients.name = 'Potato');
insert into api.ingredients_recipe
(select api.recipes.id, api.ingredients.id from api.recipes, api.ingredients where api.recipes.name = 'Patato potato' and api.ingredients.name = 'carotte');

-- roles and bindings
--- Anonymous bindings RO
create role web_anon nologin;

grant usage on schema api to web_anon;
grant select on api.recipes to web_anon;
grant select on api.ingredients to web_anon;
grant select on api.ingredients_recipe to web_anon;

create role authenticator noinherit login password 'mysecretpassword';
grant web_anon to authenticator;

--- authenticated users
create role authenticated_user nologin;
grant authenticated_user to authenticator;

grant usage on schema api to authenticated_user;
grant all on api.recipes to authenticated_user;
grant usage, select on sequence api.recipes_id_seq to authenticated_user;
