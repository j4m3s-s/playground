from back.cardsystems.models import Tag
from back.cardsystems.serializers import TagSerializer
from rest_framework import generics

class TagList(generics.ListCreateAPIView):
    queryset = Tag.objects.all()
    serializer_class = TagSerializer
