from datetime import date

from back.cardsystems.models import Tag, Card, CardTag
from rest_framework.serializers import ModelSerializer


class TagSerializer(ModelSerializer):
    class Meta:
        model = Tag
        fields = [ 'name', 'id' ]


class CardSerializer(ModelSerializer):
    class Meta:
        model = Card
        fields = [ 'front', 'back', 'id' ]

    def create(self, validated_data):
        return Card(**validated_data, sm2_next_date=date.today())


class CardTagSerializer(ModelSerializer):
    class Meta:
        model = CardTag
        fields = [ 'card', 'tag' ]
