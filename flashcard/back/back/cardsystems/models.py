from django.db.models import Model
from django.db import models

# Create your models here.

class Tag(Model):
    name = models.CharField(max_length=30, unique=True)

class Card(Model):
    # FIXME: support for images / sound ?
    front = models.CharField(max_length=500)
    back = models.CharField(max_length=500)

class CardTag(Model):
    card = models.ForeignKey(Card, on_delete=models.DO_NOTHING)
    tag  = models.ForeignKey(Tag, on_delete=models.DO_NOTHING)
