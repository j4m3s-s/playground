create schema if not exists api;

-- models
create table api.recipes (
    id serial primary key,
    name text,
    search_name tsvector GENERATED ALWAYS AS (to_tsvector('french', name)) STORED,
    cook_time interval,
    prep_time interval,
    perform_time interval,
    total_time interval,
    created date,
    modified date
);

create table api.ingredients (
    id serial primary key,
    name text,
    search_name tsvector GENERATED ALWAYS AS (to_tsvector('french', name)) STORED
);

create table api.ingredients_recipe (
    ingredient_id serial references api.ingredients(id),
    recipe_id serial references api.recipes(id),
    unit text,
    quantity integer
);
-- units :
-- - mg (although shown as g)
-- - ml (shown as cl ?)
-- - number

create table api.ustensils (
    id serial primary key,
    name text,
    search_name tsvector GENERATED ALWAYS AS (to_tsvector('french', name)) STORED
);

create table api.ustensils_recipe (
    ustensil_id serial references api.ustensils(id),
    recipe_id serial references api.recipes(id)
);

create table api.steps (
    recipe_id serial references api.recipes(id),
    text text,
    -- steps are ordered
    position serial,
    id serial primary key
);
