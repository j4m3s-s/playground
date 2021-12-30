
insert into api.recipes
(name, created, modified)
values
('Patato potato', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

insert into api.ingredients
(name)
values
('Potato');
insert into api.ingredients
(name)
values
('carotte');

insert into api.ingredients_recipe
(select api.recipes.id, api.ingredients.id from api.recipes, api.ingredients where api.recipes.name = 'Patato potato' and api.ingredients.name = 'Potato');
insert into api.ingredients_recipe
(select api.recipes.id, api.ingredients.id from api.recipes, api.ingredients where api.recipes.name = 'Patato potato' and api.ingredients.name = 'carotte');

insert into api.ustensils
(name)
values
('maryse');

insert into api.ustensils_recipe
(select api.ustensils.id, api.recipes.id from api.recipes, api.ustensils where api.recipes.name = 'Patato potato' and api.ustensils.name = 'maryse');

insert into api.steps
(text)
values
('Pour cette étape, rajouter le chocolat chaud avec la moutarde');

insert into api.steps_recipe
(select api.steps.id, api.recipes.id from api.recipes, api.steps where api.recipes.name = 'Patato potato' and api.steps.text = 'Pour cette étape, rajouter le chocolat chaud avec la moutarde');
