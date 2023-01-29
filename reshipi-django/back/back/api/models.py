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

    # FIXME: maybe there's a better way to store plaint text
    ingredients = models.CharField(max_length=5000, default="")
    ustensils = models.CharField(max_length=5000, default="")

    steps = models.CharField(max_length=5000, default="")

    #TODO: add author
    #TODO: add typed fields to make things proper
