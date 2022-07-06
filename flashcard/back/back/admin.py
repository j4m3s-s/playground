from django.contrib import admin
from back.cardsystems.models import Tag, Card, CardTag, TestWorkflow, TestWorkflowQuestion

admin.site.register(Tag)
admin.site.register(Card)
admin.site.register(CardTag)
admin.site.register(TestWorkflow)
admin.site.register(TestWorkflowQuestion)
