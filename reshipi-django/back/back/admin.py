from django.contrib import admin

from back.api.models import Recipe

admin.site.register(Recipe)
#FIXME: remove RecipeStep and make it easy to edit into admin site directly using
#Django shenanigans. Or just make so easy to modify things in the UI.
