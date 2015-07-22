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
from django.db.models import Count

from rest_framework.authentication import TokenAuthentication
from rest_framework.permissions import IsAuthenticated

class UserSearch(APIView):
    authentication_classes = (TokenAuthentication,) # Use token for authentication
    permission_classes = (IsAuthenticated,) # Only authenticated users may view the list of other users
    
    def getProject(self, pk): # Helper function for get; handles error checking
        try:
            return Projects.objects.get(pk = pk)
        except Projects.DoesNotExist:
            raise Http404
    '''
        Lists all users that a project's required skills category matches. The more relevant skills the user has, 
        the user is prioritized in the list
    '''
    def get(self, request, pk, format = None):
        project = self.getProject(pk) # Get the project using its primary key
        skills = Skills.objects.filter(projects = project.id) # All skills related to the project
        skillsList = [skill.id for skill in skills] # Make a list containing skill ids of all skills related to the project
        
        skills_users = Skills.user_profiles.through # skills / users pivot table
        # Get the userprofiles_ids which have the skills required by the project in the skills_users table 
        users = skills_users.objects.filter(skills_id__in = skillsList).values('userprofiles_id')
        userProfile_ids = users.annotate(count = Count('userprofiles_id')).order_by('-count') # order by the count of userprofiles_ids in descending order
        user_list = [userProfile_id['userprofiles_id'] for userProfile_id in userProfile_ids] # Put the ids into a list
        userProfiles = UserProfiles.objects.filter(user_id__in = user_list) # get those users' user profiles
        userProfiles_list = list(userProfiles) # Make a user profiles list
        userProfiles_list.sort(key = lambda profile: user_list.index(profile.id)) # Sort the user profiles list in the order that user-list is sorted
 
        userProfilesSerializer = UserProfilesSerializer(userProfiles_list, many = True, context = {'request': request}) # serialize the data
        return Response(userProfilesSerializer.data) # Return the response

class ProjectSearch(APIView):
    '''Returns a list of all projects the requesting user might be interested in based on their preference Types
        stored in the database
    '''
    #Set authentication and permission classes so only authorized users can request for projects
    authentication_classes = (TokenAuthentication,)
    permission_classes = (IsAuthenticated,)

    def get(self, request, format=None):
        profile = UserProfiles.objects.get(user_id=request.user.id) #obtain UserProfile from the requesting User's id 
        types_profiles = Types.users.through #the types_profiles pivot table
        preferredTypes = types_profiles.objects.filter( userprofiles_id= profile.id ) #obtain all Types preferred by the requesting user
        preferredTypes = [x.types_id for x in preferredTypes] #list of IDs of preferred types

        types_projects = Types.projects.through #the types_projects pivot table
        rawResults = types_projects.objects.filter(types_id__in=preferredTypes).values('projects_id') #All projects that match preferred types (with duplicate projects)
        projectIDs = rawResults.annotate(count=Count('projects_id')).order_by('-count') #Get the IDs of the projects in descending order of most "Type" matches
        projectIDs = [x['projects_id'] for x in projectIDs] #Put the IDs into a flat list
        projects = Projects.objects.filter(pk__in=projectIDs) #Get the actual project instances from the IDs
        projects_list = list(projects) #turn queryset into a list
        projects_list.sort(key=lambda project: projectIDs.index(project.id)) #Sort projects back to proper order based on projectIDs

        #Serialize the data and return it
        serializer = ProjectsSerializer(projects_list, many=True, context={'request': request})
        return Response( serializer.data )

from rest_framework import mixins
from rest_framework import generics

class Project(generics.GenericAPIView):
    queryset = Projects.objects.all()
    serializer_class = ProjectsSerializer

    def post(self, request, format=None ):
        serializer = ProjectsSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data )
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
        
