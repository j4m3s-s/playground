-- roles and bindings
--- Anonymous bindings RO
create role web_anon nologin;

grant usage on schema api to web_anon;
grant usage, select on api.recipes_id_seq to web_anon;
grant usage, select on api.ingredients_id_seq to web_anon;
grant usage, select on api.ingredients_recipe_recipe_id_seq to web_anon;
grant usage, select on api.ingredients_recipe_ingredient_id_seq to web_anon;

grant select on api.ustensils to web_anon;
grant select, usage on api.ustensils_id_seq to web_anon;
grant select on api.ustensils_recipe to web_anon;
grant select, usage on api.ustensils_recipe_recipe_id_seq to web_anon;
grant select, usage on api.ustensils_recipe_ustensil_id_seq to web_anon;

grant usage, select on api.steps_id_seq to web_anon;
grant usage, select on api.steps_position_seq to web_anon;

create role authenticator noinherit login password 'mysecretpassword';
grant web_anon to authenticator;

--- authenticated users
create role normal_user nologin;
grant normal_user to authenticator;


grant usage on schema api to normal_user;

grant select on api.ustensils to normal_user;
grant all on api.ustensils to normal_user;

grant usage, select on sequence api.recipes_id_seq to normal_user;
grant all on api.recipes to normal_user;

grant select on api.ustensils to normal_user;
grant all on api.ustensils to normal_user;
grant select, usage on api.ustensils_id_seq to normal_user;
grant select on api.ustensils_recipe to normal_user;
grant all on api.ustensils_recipe to normal_user;
grant select, usage on api.ustensils_recipe_recipe_id_seq to normal_user;
grant select, usage on api.ustensils_recipe_ustensil_id_seq to normal_user;

