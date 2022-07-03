from django.urls import include, path
from rest_framework import routers
from back.cardsystems import views
from django.contrib import admin

urlpatterns = [
    path('admin/', admin.site.urls),
    path('api/v1/tags', views.TagListCreate.as_view()),
    path('api-auth/', include('rest_framework.urls', namespace='rest_framework'))
]
