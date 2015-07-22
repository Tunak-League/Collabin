from django.shortcuts import render

from server.models import Projects, UserProfiles
from server.serializers import ProjectsSerializer, UsersSerializer, UserProfilesSerializer

from rest_framework import generics
from rest_framework import permissions, authentication
from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework.reverse import reverse
from rest_framework.views import APIView

from rest_framework.authentication import TokenAuthentication
from rest_framework.permissions import IsAuthenticated


class UserList(generics.ListCreateAPIView)
	authentication_classes = (TokenAuthentication)
	permission_classes = (IsAuthenticated,)
	queryset = User.objects.all()
	serializer_class = UsersSerializer
	
	def post(self, request, format=None):
		content = {
			 'user': unicode(request.user),  # `django.contrib.auth.User` instance.
            		'auth': unicode(request.auth),  # None
      		  }
        	return Response(content)
	


class UserDetail(generics.RetrieveUpdateDestroyAPIView):
	authentication_classes = (TokenAuthentication)
	permission_classes = (IsAuthenticated,)
	queryset = UserProfiles.objects.all()
	serializer_class = UsersSerializer

	def get(self, request, format=None):
		content = {
			 'user': unicode(request.user),  # `django.contrib.auth.User` instance.
            		'auth': unicode(request.auth),  # None
      		  }
        	return Response(content)
	

@api_view(('GET',))
def api_root(request, format=None):
	return Response({'users' : reverse('user-list', request=request, format=format),
			'project': reverse('project-list', request=request, format=format)})



