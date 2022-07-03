from django.shortcuts import render
from back.cardsystems.models import Tag
from back.cardsystems.serializers import TagSerializer

from django.http import Http404
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status, generics

# FIXME: authenticated endpoint
class TagListCreate(generics.ListCreateAPIView):
    queryset = Tag.objects.all()
    serializer_class = TagSerializer
