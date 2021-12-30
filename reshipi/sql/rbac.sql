-- roles and bindings
--- Anonymous bindings RO
create role web_anon nologin;

grant usage on schema api to web_anon;
grant select on api.recipes to web_anon;
grant select on api.ingredients to web_anon;
grant select on api.ingredients_recipe to web_anon;
grant select on api.ustensils to web_anon;
grant select on api.ustensils_recipe to web_anon;

create role authenticator noinherit login password 'mysecretpassword';
grant web_anon to authenticator;

--- authenticated users
create role authenticated_user nologin;
grant authenticated_user to authenticator;

grant usage on schema api to authenticated_user;
grant all on api.recipes to authenticated_user;
grant usage, select on sequence api.recipes_id_seq to authenticated_user;
