
insert into api.recipes
(name, search_name, created, modified)
values
('Patato potato', 'Patato potato', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

insert into api.ingredients
(name, search_name)
values
('Potato', 'Potato');
insert into api.ingredients
(name, search_name)
values
('carotte', 'carotte');

insert into api.ingredients_recipe
(select api.recipes.id, api.ingredients.id from api.recipes, api.ingredients where api.recipes.name = 'Patato potato' and api.ingredients.name = 'Potato');
insert into api.ingredients_recipe
(select api.recipes.id, api.ingredients.id from api.recipes, api.ingredients where api.recipes.name = 'Patato potato' and api.ingredients.name = 'carotte');

insert into api.ustensils
(name, search_name)
values
('maryse', 'maryse');

insert into api.ustensils_recipe
(select api.ustensils.id, api.recipes.id from api.recipes, api.ustensils where api.recipes.name = 'Patato potato' and api.ustensils.name = 'maryse');

