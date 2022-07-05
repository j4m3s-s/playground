from django.db.models import Model
from django.db import models

# Create your models here.

class Tag(Model):
    name = models.CharField(max_length=30, unique=True)

    def __str__(self):
        return self.name

class Card(Model):
    # FIXME: support for images / sound ?
    front = models.CharField(max_length=500)
    back = models.CharField(max_length=500)

    # For SM2 algorithm
    sm2_interval = models.DateField(auto_now=True)
    sm2_repetition_number = models.IntegerField(default=0)
    sm2_easiness_factor = models.FloatField(default=2.5)

    def __str__(self):
        return f'{self.front} - {self.back}'

class CardTag(Model):
    card = models.ForeignKey(Card, on_delete=models.DO_NOTHING)
    tag  = models.ForeignKey(Tag, on_delete=models.DO_NOTHING)

    class Meta:
        unique_together = ('card', 'tag')

    def __str__(self):
        return f'{str(self.card)} -- {self.tag.name}'


class TestWorkflow(Model):
    date = models.DateField(auto_now=True)

class TestWorkflowQuestions(Model):
    card = models.ForeignKey(Card, on_delete=models.DO_NOTHING)
    test_workflow = models.ForeignKey(TestWorkflow, on_delete=models.DO_NOTHING)
