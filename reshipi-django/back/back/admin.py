from django.contrib import admin

from back.api.models import Recipe, Ingredient, Ustensil

admin.site.register(Recipe)
admin.site.register(Ingredient)
admin.site.register(Ustensil)
