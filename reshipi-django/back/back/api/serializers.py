from django.contrib.auth.models import User, Group
from rest_framework import serializers
from rest_framework.serializers import ModelSerializer
from back.api.models import Recipe


class RecipeSerializer(ModelSerializer):
    class Meta:
        model = Recipe
        fields = ["id", "name", "cook_time", "prep_time", "perform_time",
                  "total_time", "created", "modified", "ingredients",
                  "ustensils", "steps"]
        # Recursively serialize models
        depth = 1
