from django.contrib.auth.models import User, Group
from rest_framework import viewsets
from rest_framework.permissions import IsAuthenticated
from rest_framework.generics import ListAPIView, RetrieveUpdateDestroyAPIView

from back.api.models import Ingredient, Recipe, Ustensil
from back.api.serializers import RecipeSerializer, IngredientSerializer, UstensilSerializer

class AuthenticatedView:
    permission_classes = [ IsAuthenticated ]

class RecipeList(ListAPIView):
    queryset = Recipe.objects.all()
    serializer_class = RecipeSerializer

class RecipeEdit(AuthenticatedView, RetrieveUpdateDestroyAPIView):
    queryset = Recipe.objects.all()
    # TODO: Can we edit subobjects here and have them modify inside the db
    # directly ?
    # I'm thinking specifically about recipe steps, as well as adding refs to
    # ustensils / ingredients
    serializer_class = RecipeSerializer

class IngredientList(ListAPIView):
    queryset = Ingredient.objects.all()
    serializer_class = IngredientSerializer

class IngredientEdit(AuthenticatedView, RetrieveUpdateDestroyAPIView):
    queryset = Ingredient.objects.all()
    serializer_class = IngredientSerializer

class UstensilList(ListAPIView):
    queryset = Ustensil.objects.all()
    serializer_class = UstensilSerializer

class UstensilEdit(AuthenticatedView, RetrieveUpdateDestroyAPIView):
    queryset = Ustensil.objects.all()
    serializer_class = UstensilSerializer
