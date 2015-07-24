#from django.shortcuts import render

from django.contrib.auth.models import User

from server.models import UserProfiles, Skills, Types, Projects
from server.serializer import UsersSerializer, UserProfilesSerializer, SkillsSerializer, TypesSerializer, ProjectsSerializer

from rest_framework import permissions
from rest_framework import generics

from server.permissions import IsOwnerOrReadOnly

"""
from rest_framework import generics
from rest_framework import permissions, authentication
from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework.reverse import reverse
from rest_framework.views import APIView

from rest_framework.authentication import TokenAuthentication
from rest_framework.permissions import IsAuthenticated
"""
##############
class UserList(generics.ListCreateAPIView):
	#permission_classes = (IsAuthenticatedOrReadOnly,)
	#authentication_classes = (TokenAuthentication)
	queryset = User.objects.all()
	serializer_class = UsersSerializer
	

	
"""
	def post(self, request, format=None):
		content = {
			 'user': unicode(request.user),  # `django.contrib.auth.User` instance.
            		'auth': unicode(request.auth),  # None
      		  }
        	return Response(content)
	
"""

class UserDetail(generics.RetrieveUpdateDestroyAPIView):
	#authentication_classes = (TokenAuthentication)
	#permission_classes = (IsAuthenticated,)
	queryset = UserProfiles.objects.all()
	serializer_class = UsersSerializer
"""
	def get(self, request, format=None):
		content = {
			 'user': unicode(request.user),  # `django.contrib.auth.User` instance.
            		'auth': unicode(request.auth),  # None
      		  }
        	return Response(content)
	
"""
##############
class SkillsList(generics.ListCreateAPIView):
	queryset = Skills.objects.all()
	serializer_class = SkillsSerializer
	permission_class = (permissions.IsAuthenticatedOrReadOnly,)
	
	def perform_create(self, serializer):
		serializer.save(users=self.request.users)
	def perform_create(self, serializer):
		serializer.save(projects=self.request.projects)

class SkillsDetail(generics.RetrieveUpdateDestroyAPIView):
	queryset = Skills.objects.all()
	serializer_class = SkillsSerializer
	permission_classes = (permissions.IsAuthenticatedOrReadOnly,IsOwnerOrReadOnly,)

##############
class Types(generics.ListCreateAPIView):
	queryset = Types.objects.all()
	serializer_class = TypesSerializer
	permission_class = (permissions.IsAuthenticatedOrReadOnly,)
	
	def perform_create(self, serializer):
		serializer.save(users=self.request.users)

class TypesDetail(generics.RetrieveUpdateDestroyAPIView):
	queryset = Types.objects.all()
	serializer_class = TypesSerializer
	permission_classes = (permissions.IsAuthenticatedOrReadOnly,IsOwnerOrReadOnly,)
##############

class Projects(generics.ListCreateAPIView):
	queryset = Projects.objects.all()
	serializer_class = ProjectsSerializer
	permission_class = (permissions.IsAuthenticatedOrReadOnly,)
	
	def perform_create(self, serializer):
		serializer.save(owner=self.request.owner)
	def perform_create(self, serializer):
		serializer.save(types=self.request.types)

class  ProjectsDetail(generics.RetrieveUpdateDestroyAPIView):
	queryset = Types.objects.all()
	serializer_class = TypesSerializer
	permission_classes = (permissions.IsAuthenticatedOrReadOnly,IsOwnerOrReadOnly,)
	
##############


from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework.reverse import reverse

@api_view(('GET',))
def api_root(request, format=None):
	return Response({'users' : reverse('user-list', request=request, format=format),
			'project': reverse('project-list', request=request, format=format)})


