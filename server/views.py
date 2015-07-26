
from django.shortcuts import render
from django.http import Http404
from django.db.models import Count

from django.contrib.auth.models import User
from server.models import UserProfiles, Skills, Types, Projects
from server.serializer import UsersSerializer, SkillsSerializer, UserProfilesSerializer, TypesSerializer, ProjectsSerializer

from rest_framework import generics
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framwork import status


from rest_framework.authentication import TokenAuthentication
from rest_framework.permissions import IsAuthenticated

import django_filters

#returns the specified user
class UserSearch(django_filters.FilterSet):

#only users can search for other users
	authentication_classes = (TokenAuthentication,)
	permission_classes = (IsAuthenticated,)

#reference: http://www.django-rest-framework.org/api-guide/filtering/#searchfilter
	queryset = User.objects.all()
	serializer = UserSerializer
	filter_bakends = (filters.SearchFilter,)
	search_fields = ('username','first_name', 'last_name')
	

		
from rest_framework import mixins
from rest_framwork import generics

class Users(generics.GenericAPIVIew):
	queryset = User.objects.all()
	serializer_class = UsersProfilesSerializer
	
	def post(self, request, format=None):
		serializer= UsersProfilesSerializer(data=request.data)
		if serializer.is_valid():
			serializer.save()
			return Response(serializer.data)
		return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
