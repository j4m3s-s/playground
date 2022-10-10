from django.db import models
from django.db.models import Model

# Create your models here.
# TODO:
# - create meta object that records all changes into a json à la ChangeLog
# (netbox does it online). Very kafkaesque way of storing data.
# - Also ordered by ID so it makes the API easier to work with.
# - Also has a __name__

class Recipe(Model):
    name = models.CharField(max_length=64)
    cook_time = models.DurationField()
    prep_time = models.DurationField()
    perform_time = models.DurationField()
    total_time = models.DurationField()

    created = models.DateField()
    modified = models.DateField()

    ingredients = models.ManyToManyField("Ingredient")
    ustensils = models.ManyToManyField("Ustensil")

    steps = models.ManyToManyField("RecipeStep")

    #TODO: add author

class Ingredient(Model):
    name = models.CharField(max_length=64)

class Ustensil(Model):
    name = models.CharField(max_length=64)

class RecipeStep(Model):
    # Let's be C logical. Begins at 0, up to whatever integer size this is.
    position = models.IntegerField()
    text = models.CharField(max_length=500)
