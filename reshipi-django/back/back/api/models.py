from django.db import models
from django.db.models import Model

# Create your models here.
# TODO:
# - create meta object that records all changes into a json à la ChangeLog
# (netbox does it online). Very kafkaesque way of storing data.
# - Also ordered by ID so it makes the API easier to work with.
# - Also has a __name__

class MetaObject:
    def __str__(self):
        return self.name if "name" in self.__dict__ else str(super())

class Recipe(MetaObject, Model):
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

class Ingredient(MetaObject, Model):
    name = models.CharField(max_length=64)

class Ustensil(MetaObject, Model):
    name = models.CharField(max_length=64)

class RecipeStep(MetaObject, Model):
    # Let's be C logical. Begins at 0, up to whatever integer size this is.
    position = models.IntegerField()
    text = models.CharField(max_length=500)

    def __str__(self):
        # Let's shorten it so that it's readable in the admin
        return f"{self.position} - {self.text[:25]}..."
