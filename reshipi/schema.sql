create schema if not exists api;

-- models
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

create table api.ingredients (
    id serial primary key,
    name text,
    search_name tsvector
);

create table api.ingredients_recipe (
    recipe_id serial references api.recipes(id),
    ingredient_id serial references api.ingredients(id)
);

create table api.ustensils (
    id serial primary key,
    name text,
    search_name tsvector
);

create table api.ustensils_recipe (
    ustensil_id serial references api.ustensils(id),
    recipe_id serial references api.recipes(id)
);
