from server.models import Projects, UserProfiles, Skills, Types, Swipes
from server.serializers import ProjectsSerializer, UserProfilesSerializer, TypesSerializer, SkillsSerializer, SwipesSerializer, ProjectMatchSerializer

from django.http import Http404
from django.db.models import Count
from django.contrib.auth.models import User

from rest_framework import generics, mixins, status
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework.authentication import TokenAuthentication
from rest_framework.permissions import IsAuthenticated
from rest_framework.decorators import api_view
from datetime import date

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
        userProfiles = UserProfiles.objects.filter(pk__in = user_list) # get those users' user profiles
        userProfiles_list = list(userProfiles) # Make a user profiles list
        userProfiles_list.sort(key = lambda profile: user_list.index(profile.id)) # Sort the user profiles list in the order that user-list is sorted
        
        userProfilesSerializer = UserProfilesSerializer(userProfiles_list, many = True, context = {'request': request}) # serialize the data
        return Response(userProfilesSerializer.data) # Return the response

class ProjectSearch(APIView):
    '''
        Returns a list of all projects the requesting user might be interested in based on their preference Types
        stored in the database
    '''
    #Set authentication and permission classes so only authorized users can request for projects
    authentication_classes = (TokenAuthentication,)
    permission_classes = (IsAuthenticated,)

    def get(self, request, format=None):
        profile = UserProfiles.objects.get(user_id=request.user.id) #obtain UserProfile from the requesting User's id 
        types_profiles = Types.user_profiles.through #the types_profiles pivot table
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



class ProjectList(generics.GenericAPIView, mixins.CreateModelMixin):
    authentication_classes = (TokenAuthentication,)
    permission_classes = (IsAuthenticated,)
    queryset = Projects.objects.all()
    serializer_class = ProjectsSerializer
    
    # Override the perform_create function to automatically set the project's owner as the requesting user 
    def perform_create(self, serializer):
        serializer.save(owner = UserProfiles.objects.get(user_id = self.request.user.id))
    
    # GET request of all projects a user owns
    def get(self, request, *args, **kwargs):
        profile = UserProfiles.objects.get(user_id = request.user.id) # Get the requesting user's profile
        projects = Projects.objects.filter(owner_id = profile.id) # get projects owned by the requesting user
        serializer = ProjectsSerializer(projects, many = True, context = {'request': request}) # deserialize the data
        return Response(serializer.data) # return the requested data
    

    # Creates a new Project with the specified fields
    def post(self, request, *args, **kwargs ):
        request.data['date_created'] = date.today() # Set the date_created field as today's date
        skillsList = request.data.getlist('skills') # Get a list of all skills associated with this project

        # Check if skills exist in database, create them if they don't. Check for errors after
        if check_skills(skillsList) == False: 
            return Response( status=status.HTTP_400_BAD_REQUEST ) #TODO: Change to correct code + MORE SPECIFIC DETAILS FOR CLIENT '''
        
        # If the user requests to create a project without specifying the types of the project, send an error message response
        if not request.data.get('types'):
            content = {'Please specify the type of the project. You must specify at least one type to create a project'}
            return Response(content, status = status.HTTP_404_NOT_FOUND)

        # If the user requests to create a project without specifying the skills of the project, send an error message response
        if not request.data.get('skills'):
            content = {'Please specify the skills required for this project. You must specify at least one skill to create a project'}
            return Response(content, status = status.HTTP_404_NOT_FOUND)
        
        return self.create(request, *args, **kwargs ) # Use CreateModelMixin to create the Project

# Update, Retrieve or Delete an existing project specified by its primary key 
class ProjectDetail(generics.GenericAPIView, mixins.UpdateModelMixin, mixins.RetrieveModelMixin, mixins.DestroyModelMixin):
    authentication_classes = (TokenAuthentication,)
    permission_classes = (IsAuthenticated,)
    queryset = Projects.objects.all()
    serializer_class = ProjectsSerializer

    # Update data for a specific Project
    def put(self, request, *args, **kwargs): 
        skillsList = request.data.getlist('skills') #Get a list of all skills associated with this project

        #Check if skills exist in database, create them if they don't. Check for errors after
        if check_skills(skillsList) == False: 
            return Response( status=status.HTTP_400_BAD_REQUEST ) #TODO: Change to correct code + MORE SPECIFIC DETAILS FOR CLIENT '''

        return self.update(request, *args, **kwargs )
    
    # Get data for a specific project
    def get(self, request, *args, **kwargs):
        return self.retrieve(request, *args, **kwargs)
    
    # Delete a specific project
    def delete(self, request, *args, **kwargs):
        return self.destroy(request, *args, **kwargs)

'''
    Helper function to check if a list of Skills exist in the database and create them if they don't
    @param skillsList - list of skills 
    @postcondition - Return true if skills either all exist OR were successfully created. False if an error occurs when creating a skill
'''
def check_skills(skillsList): 
    for skill in skillsList: 
        try:
            Skills.objects.get(skill_name=skill) # Check if this skill exists in database
        except Skills.DoesNotExist:
            skillData = {}
            skillData['skill_name'] = skill # Format the skill as a dictionary to pass to SkillsSerializer
            serializer = SkillsSerializer(data=skillData)
            if serializer.is_valid(): 
                serializer.save() # Save newly created skill to database
            else:
                return False # Data was invalid 
    return True # all skills either creatd or already exist

'''
    Returns all projects matched with the requesting user
'''
@api_view(['GET'])
def project_matches(request):
    authentication_classes = (TokenAuthentication,)
    permission_classes = (IsAuthenticated,)
    
    profile = UserProfiles.objects.get(user=request.user)
    matched_swipes = Swipes.objects.filter( user_profile=profile, user_likes=Swipes.YES, project_likes=Swipes.YES ) #get all swipes that user is involved in with mutual likes
    serializer = ProjectMatchSerializer(matched_swipes, many=True, context = {'request': request} )
    return Response( serializer.data )

'''
    Returns all users matched with the requesting project owner
'''
@api_view(['GET'])
def user_matches(request):
    authentication_classes = (TokenAuthentication,)
    permission_classes = (IsAuthenticated,)
    
    profile = UserProfiles.objects.get(user = request.user)
    projects = Projects.objects.filter(owner = profile)
    projectsList = [project.id for project in projects]
    matched_swipes = Swipes.objects.filter(project_id__in = projectsList, user_likes = Swipes.YES, project_likes = Swipes.YES)
    serializer = UserMatchSerializer(matched_swipes, many = True, context = {'request': request})
    return Response(serializer.data)

@api_view(['POST'])
def user_list(request):
    authentication_classes = (TokenAuthentication,)
    permission_classes = (IsAuthenticated,)

    user = User.objects.create_user(request.data.get('username'), request.data.get('email'), request.data.get('password'))
    requestData = request.data.copy()
    requestData['user'] = user.id
    
    skillsList = request.data.getlist('skills') # Get a list of all skills associated with this user
    if not check_skills(skillsList): 
        return Response(status=status.HTTP_400_BAD_REQUEST) #TODO: Change to correct code + MORE SPECIFIC DETAILS FOR CLIENT '''
    
    serializer = UserProfilesSerializer(data = requestData)
    if serializer.is_valid():
        serializer.save()
        return Response(serializer.data, status = status.HTTP_201_CREATED)
    return Response(serializer.errors, status = status.HTTP_400_BAD_REQUEST)

class UserDetail(APIView):
    authentication_classes = (TokenAuthentication,)
    permission_classes = (IsAuthenticated,)

    def put(self, request, format = None):
        profile = UserProfiles.objects.get(user_id = request.user.id)
        requestData = request.data.copy()
        requestData['user'] = profile.user_id

        skillsList = request.data.getlist('skills') # Get a list of all skills associated with this user
        if not check_skills(skillsList): 
            return Response(status=status.HTTP_400_BAD_REQUEST) #TODO: Change to correct code + MORE SPECIFIC DETAILS FOR CLIENT '''

        serializer = UserProfilesSerializer(profile, data = requestData)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data)
        return Response(serializer.errors, status = status.HTTP_400_BAD_REQUEST)

    def delete(self, request, format = None):
        user = User.objects.get(id = request.user.id)
        profile = UserProfiles.objects.get(user_id = user.id)
        user.delete()
        profile.delete()
        return Response(status = status.HTTP_204_NO_CONTENT)
