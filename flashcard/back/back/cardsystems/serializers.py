from datetime import date

from back.cardsystems.models import Tag, Card, CardTag
from rest_framework.serializers import ModelSerializer
from rest_framework import serializers
from django.contrib.auth.models import User

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


class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = User

        fields = (
            "username",
            "first_name",
            "last_name",
            "email",
            "address",
            "city",
            "zip_code",
            "country",
            "is_staff",
        )

        read_only_fields = (
            "first_name",
            "last_name",
            "email",
            "username",
            "is_staff",
        )


class AuthenticationSerializer(serializers.Serializer):
    username = serializers.CharField(max_length=64)
    password = serializers.CharField(max_length=64)
