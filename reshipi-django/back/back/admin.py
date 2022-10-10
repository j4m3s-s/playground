from django.contrib import admin

from back.api.models import Recipe, Ingredient, RecipeStep, Ustensil

admin.site.register(Recipe)
admin.site.register(Ingredient)
admin.site.register(Ustensil)

#FIXME: remove this and make it easy to edit into admin site directly using
#Django shenanigans.
admin.site.register(RecipeStep)
