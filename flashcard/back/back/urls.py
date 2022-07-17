from django.urls import include, path
from rest_framework import routers
from back.cardsystems import views
from django.contrib import admin

urlpatterns = [
    path('admin/', admin.site.urls),
    # Tags
    path('api/v1/tags', views.TagList.as_view()),
    path('api/v1/tag/', views.TagCreate.as_view()),
    path('api/v1/tag/<int:pk>', views.TagEdit.as_view()),

    # Cards
    path('api/v1/cards', views.CardList.as_view()),
    path('api/v1/card/', views.CardCreate.as_view()),
    path('api/v1/card/<int:pk>', views.CardEdit.as_view()),

    # CardTag
    path('api/v1/cardtags/', views.CardTagList.as_view()),
    path('api/v1/cardtag/<int:id>', views.CardTagGet.as_view()),

    # TestingWorkflow
    path('api/v1/testingworkflow/sm2/', views.TestingWorkflowSM2.as_view()),


    # TestingworkflowQuestions
    path('api/v1/testingworkflowquestions/sm2/<int:id>', views.TestingWorkflowQuestionsSM2.as_view()),

    path('api-auth/', include('rest_framework.urls', namespace='rest_framework')),

    path('__debug__/', include('debug_toolbar.urls')),
]
