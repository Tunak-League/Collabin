from django.shortcuts import render
from server.models import Projects, UserProfiles
from server.serializers import ProjectsSerializer
from rest_framework import generics



from django.http import Http404
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status
from server.models import Projects
from server.models import Types
from server.serializers import ProjectsSerializer

class ProjectList(APIView):
    '''Returns a list of all projects the requesting user might be interested in based on their preference Types
        stored in the database
    '''
    def get(self, request, format=None):
        print "HALP ME:" + request.user.username
        profile = UserProfiles.objects.get(user_id=request.user.id) #obtain UserProfile from the requesting User's id 
        types_profiles = Types.users.through #the types_profiles pivot table
        preferredTypes = types_profiles.objects.filter( userprofiles_id= profile.id ) #obtain all Types preferred by the requesting user
        preferredTypes = [x.types_id for x in preferredTypes] 

        projects = Projects.objects.filter( id_types__in=preferredTypes ) #obtain all projects with a type matching an item in preferredTypes
        print "HEY OVER HERE:" + projects[3].project_name
        #Serialize the data and return it
        serializer = ProjectsSerializer(projects, many=True, context={'request': request})
        return Response( serializer.data )
