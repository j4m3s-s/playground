create schema api;

-- models
drop table api.recipes;
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

-- roles and bindings
--- Anonymous bindings RO
create role web_anon nologin;

grant usage on schema api to web_anon;
grant select on api.recipes to web_anon;
grant select on public to web_anon;

create role authenticator noinherit login password 'mysecretpassword';
grant web_anon to authenticator;

--- authenticated users
create role authenticated_user nologin;
grant authenticated_user to authenticator;

grant usage on schema api to authenticated_user;
grant all on api.recipes to authenticated_user;
grant usage, select on sequence api.recipes_id_seq to authenticated_user;
