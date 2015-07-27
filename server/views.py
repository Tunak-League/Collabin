from django.contrib.auth.models import User
from server.models import UserProfiles, Skills, Types, Projects
from server.serializer import UsersSerializer, SkillsSerializer, UserProfilesSerializer, TypesSerializer, ProjectsSerializer

from django.http import Http404

from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status

class Users(APIView):
	"""
	list the user or create a new user
	"""
	def get(self, request, format=None):
		user = User.objects.all()
		serializer = UsersSerializer(user)
		return Response(serializer.data)
	
	def post(self, request, format=None):
		#first create a user with the request
		serializer = UsersSerializer(data=request.data)
		if serializer.is_valid():
			serializer.save()
			#secondly, create a user profile with the request 
			serializer = UserProfilesSerializer(data=request.data)
				if serializer.is_valid():
					serializer.save()
				return Response(serializer.data, status=status.HTTP_201_CREATED)
		return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


class UserDetail(APIView):
	"""
	Retrieve, update or delete a user
	"""
	def get_object(self, pk):
		try:
			return UserProfiles.objects.get(pk=pk)
		except User.DoesNotExist:
			raise Http404
	def get(self, reqeust, pk, format=None):
		user = self.get_object(pk)
		serializer = UsersProfilesSerializer(user)
		return Response(serializer.data)

	def put(self, request, pk , format=None):
		user = self.get_object(pk)
		serializer = UsersProfilesSerializer(user.data=request.data)
		if serializer.is_valid():
			serializer.save()
			return Response(serializer.data)
		return Response(serialzier.errors, status=status.HTTP_400_BAD_REQUEST)

	def delete(self, request, pk, format=None):
		user = self.get_object(pk)
		user.delete()
		return Response(status=status.HTTP_204_N0_CONTENT)
