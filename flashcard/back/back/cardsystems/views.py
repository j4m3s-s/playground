from collections import OrderedDict

from django.shortcuts import render
from django.db.models import Q
from django.http import Http404
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status, generics

from back.cardsystems.models import Tag, Card, CardTag
from back.cardsystems.serializers import TagSerializer, CardSerializer, CardTagSerializer

# FIXME: authenticated endpoint
class TagList(generics.ListAPIView):
    queryset = Tag.objects.all()
    serializer_class = TagSerializer


# FIXME: authenticated endpoint
class TagCreate(generics.RetrieveUpdateDestroyAPIView):
    queryset = Tag.objects.all()
    serializer_class = TagSerializer


# FIXME: authenticated endpoint
class TagEdit(generics.RetrieveUpdateDestroyAPIView):
    queryset = Tag.objects.all()
    serializer_class = TagSerializer


# FIXME: authenticated endpoint
class CardList(generics.ListAPIView):
    queryset = Card.objects.all()
    serializer_class = CardSerializer


# FIXME: authenticated endpoint
class CardCreate(generics.CreateAPIView):
    queryset = Card.objects.all()
    serializer_class = CardSerializer


# FIXME: authenticated endpoint
class CardEdit(generics.RetrieveUpdateDestroyAPIView):
    queryset = Card.objects.all()
    serializer_class = CardSerializer


# FIXME: authenticated endpoint
class CardTagDelete(generics.DestroyAPIView):
    queryset = CardTag.objects.all()
    serializer_class = CardTagSerializer

# FIXME: authenticated endpoint
class CardTagCreate(generics.CreateAPIView):
    queryset = CardTag.objects.all()
    serializer_class = CardTagSerializer

# FIXME: authenticated endpoint
# TODO: filter by tags?
class CardTagList(APIView):
    def get(self, request, format=None):
        # prefetch in one query related queries
        cardstags = CardTag.objects.all().select_related('card').select_related('tag')

        # FIXME: not optimized (technically we should *not* query the whole db
        # everytime)
        cards_related_tags = OrderedDict()
        for cardtag in cardstags:
            card_serializer = CardSerializer(cardtag.card)
            card = card_serializer.data

            if cardtag.card.id not in cards_related_tags:
                cards_related_tags[cardtag.card.id] = card
                cards_related_tags[cardtag.card.id]['tags'] = []

            tag_serializer = TagSerializer(cardtag.tag)
            tag = tag_serializer.data

            cards_related_tags[cardtag.card.id]['tags'].append(tag)

        result = []
        for k, v in cards_related_tags.items():
            result.append(v)
        return Response(result)
