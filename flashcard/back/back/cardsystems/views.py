from collections import OrderedDict, defaultdict
from math import floor
from datetime import date, timedelta
import json

from django.shortcuts import render
from django.db.models import Q
from django.db import transaction
from django.http import HttpResponseBadRequest
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status, generics

from back.cardsystems.models import Tag, Card, CardTag, TestWorkflow, TestWorkflowQuestion
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
# TODO: remove this to avoid querying the whole db
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

class CardTagGet(APIView):
    def get(self, request, id=None):
        if id is None:
            return HttpResponseBadRequest()
        cardtags = CardTag.objects.filter(card__id__exact=id).select_related('tag').select_related('card')
        tags_result = []
        for cardtag in cardtags:
            tags_result.append({
                "id": cardtag.id,
                "name": cardtag.tag.name
            })

        return Response(tags_result)

# FIXME: authenticated endpoint
class CardTagList(APIView):
    def get(self, request, format=None):
        # We need at least one tag
        if not (tag1 := self.request.query_params.get('tag1')):
            return HttpResponseBadRequest()

        # Get tags
        tags = Q(tag__name__exact=tag1)
        tags_dict = { tag1: 0 }
        for tag in ["tag2", "tag3", "tag4", "tag5", "tag6"]:
            if not (tagvalue := self.request.query_params.get(tag, None)):
                break
            tags_dict[tagvalue] = 0
            tags |= Q(tag__name__exact=tagvalue)


        # Get all Card that corresponds to tags
        cardstags_from_tags = CardTag.objects.filter(tags).select_related('card')
        if not len(cardstags_from_tags):
            return HttpResponseBadRequest()

        cards_tags_filter = defaultdict(lambda: {})
        for cardstag in cardstags_from_tags:
            cards_tags_filter[cardstag.card.id][cardstag.tag.name] = 0

            # Not sure how to initialize next loop otherwise
            last_id = cardstag.card.id

        cards_filtered = {}
        for card_id, tags in cards_tags_filter.items():
            if tags != tags_dict:
                continue

            cards_filtered[card_id] = 0
            last_id = card_id

        cards = Q(card__id__exact=last_id)
        for card_id, _ in cards_filtered.items():
            cards |= Q(card__id__exact=card_id)

        # Get all tags associated with those tags
        cardstags = CardTag.objects.filter(cards).select_related('card').select_related('tag')

        cards_related_tags = OrderedDict()
        for cardtag in cardstags:
            card_serializer = CardSerializer(cardtag.card)
            card = card_serializer.data

            # Create dict for cards
            if cardtag.card.id not in cards_related_tags:
                cards_related_tags[cardtag.card.id] = card
                cards_related_tags[cardtag.card.id]['tags'] = []

            tag_serializer = TagSerializer(cardtag.tag)
            tag = tag_serializer.data['name']

            # Add related tags to each card
            cards_related_tags[cardtag.card.id]['tags'].append(tag)

        result = []
        for k, v in cards_related_tags.items():
            result.append(v)
        return Response(result)


# FIXME: use only named arguments
# Algorithm originated from supermemo, similar is used by Anki with values for
# interval that are tweaked.
def sm2(user_grade, repetition_number, easiness_factor, interval):
    """
    Answer :
    - 0: "Total blackout", complete failure to recall the information.
    - 1: Incorrect response, but upon seeing the correct answer it felt familiar.
    - 2: Incorrect response, but upon seeing the correct answer it seemed easy to remember.
    - 3: Correct response, but required significant effort to recall.
    - 4: Correct response, after some hesitation.
    - 5: Correct response with perfect recall.
    """
    # Good answer
    if user_grade >= 3:
        # Maybe tweak those values
        if repetition_number == 0:
            interval = 1
        elif repetition_number == 1:
            # Default is 6, I've chosen 3 to ease the learning phase
            interval = 3
        else:
            interval = floor(interval * easiness_factor)
        repetition_number += 1
    # Incorrect response
    else:
        repetition_number = 0
        interval = 1

    easiness_factor = easiness_factor + \
        (0.1 - (5 - user_grade) * (0.08 + (5 - user_grade) * 0.02))
    if easiness_factor < 1.3:
        easiness_factor = 1.3

    return {
        "repetition_number": repetition_number,
        "easiness_factor": easiness_factor,
        "interval": interval
    }

# FIXME: authenticated endpoint
# FIXME: make endpoint algorithm agnostic
class TestingWorkflowSM2(APIView):
    # FIXME: add tags support to filter questions -- refacto function tags
    # extraction?
    def post(self, request, format=None):
        with transaction.atomic():
            # Let's take 20 questions
            cards = Card.objects.filter(sm2_next_date__lte=date.today())[:20]

            # Create a workflow
            test_workflow = TestWorkflow()
            test_workflow.save()

            # Associate questions with it
            for card in cards:
                question = TestWorkflowQuestion(card=card, test_workflow=test_workflow)
                question.save()

        # This is used by client to query questions afterwards
        return Response({ "id": test_workflow.id})

class TestingWorkflowQuestionsSM2(APIView):
    def post(self, request, id=None):
        # TODO: use serializer here
        #card_id = self.request.query_params.get("card_id", None)
        #user_grade = self.request.query_params.get("user_grade", None)
        #test_workflow_question_id = self.request.query_params.get("test_workflow_question_id", None)
        # FIXME: use serializer as this is unsafe
        body_unicode = request.body.decode('utf-8')
        body = json.loads(body_unicode)
        card_id = body['card_id']
        user_grade = body['user_grade']
        test_workflow_question_id = body['test_workflow_question_id']

        # card id, testworkflow id, answer = [0 5]
        if (card_id is None or
            test_workflow_question_id is None or
            user_grade is None):
            return HttpResponseBadRequest()

        user_grade = int(user_grade)

        card = Card.objects.filter(id__exact=card_id)[:1][0]
        test_workflow_question = \
        TestWorkflowQuestion.objects.filter(test_workflow__id__exact=test_workflow_question_id,
                                            card_id__exact=card_id)[:1][0]

        with transaction.atomic():
            repetition_number = card.sm2_repetition_number
            easiness_factor = card.sm2_easiness_factor
            interval = card.sm2_interval

            res = sm2(user_grade=user_grade,
                      repetition_number=repetition_number,
                      easiness_factor=easiness_factor,
                      interval=interval)

            card.sm2_repetition_number = res["repetition_number"]
            card.sm2_easiness_factor = res["easiness_factor"]
            card.sm2_interval = res["interval"]
            card.sm2_next_date = timedelta(days=res["interval"]) + date.today()

            test_workflow_question.done = True
            card.save()
            test_workflow_question.save()

        return Response({})

    def get(self, request, id=None):
        if id is None:
            return HttpResponseBadRequest()

        test_workflow = TestWorkflow.objects.filter(id__exact=id)[:1]
        # Get all questions yet to ask
        question = \
        TestWorkflowQuestion.objects.filter(test_workflow__exact=test_workflow, done__exact=False).select_related('card')[:1]
        if len(question) == 0:
            return Response({})

        return Response({ "id": question[0].id })
