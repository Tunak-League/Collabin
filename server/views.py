from django.shortcuts import render
from server.models import Projects, UserProfiles
from server.serializers import ProjectsSerializer
from rest_framework import generics

class UserList(generics.ListAPIView):

