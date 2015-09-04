from server.models import Projects, UserProfiles, Skills, Types, Swipes
from server.serializers import UsersSerializer, ProjectsSerializer, UserProfilesSerializer, TypesSerializer, SkillsSerializer, SwipesSerializer, ProjectMatchSerializer, UserMatchSerializer

from django.http import Http404
from django.db.models import Count
from django.contrib.auth.models import User
from django.db.models import Q

from rest_framework import generics, mixins, status
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework.authentication import TokenAuthentication
from rest_framework.permissions import IsAuthenticated
from rest_framework.decorators import api_view
from datetime import date, datetime

from push_notifications.models import GCMDevice
from server.permissions import IsOwnerOrReadOnly
'''
    Lists all users that a project's required skills category matches. The more relevant skills the user has, 
    the user is prioritized in the list
'''
class ProjectSearch(APIView):
    authentication_classes = (TokenAuthentication,) # Use token for authentication
    permission_classes = (IsAuthenticated,) # Only authenticated users may view the list of other users

    def get(self, request, pk, format = None):
        project = getProject(pk) # Get the project using its primary key
        if not isOwner(request, project):
            return Response(request.data, status = status.HTTP_403_FORBIDDEN)
        self.check_object_permissions(self.request, project)
        skills = Skills.objects.filter(projects = project.id) # All skills related to the project
        skillsList = [skill.id for skill in skills] # Make a list containing skill ids of all skills related to the project 

        # If no preferred skills are specified on the requesting project, return all users (excluding the project's owner)
        if not skillsList:
            userProfiles = UserProfiles.objects.all().exclude(id = project.owner.id)
            serializer = UserProfilesSerializer(userProfiles, many = True)
            return Response(serializer.data)

        skills_users = Skills.user_profiles.through # skills / users pivot table 
        # Get the userprofiles_ids which have the skills required by the project in the skills_users table (excluding the project's owner) 
        users = skills_users.objects.filter(skills_id__in = skillsList).exclude(userprofiles_id = project.owner).values('userprofiles_id')

        userProfile_ids = users.annotate(count = Count('userprofiles_id')).order_by('-count') # Order by the count of userprofiles_ids in descending order
        user_list = [userProfile_id['userprofiles_id'] for userProfile_id in userProfile_ids] # Put the ids into a list
        userProfiles = UserProfiles.objects.filter(pk__in = user_list) # Get those users' user profiles
        # Get all userprofile ids that have been swiped by the project already
        usersAlreadySwiped = Swipes.objects.filter(Q(project_likes=Swipes.YES) | Q(project_likes=Swipes.NO),
                project_id = project.id, user_profile_id__in = users).values('user_profile_id')
        # Get all userprofile ids that have the skills required by the project that also have not been swiped yet 
        userProfiles = userProfiles.filter(pk__in = users).exclude(pk__in = usersAlreadySwiped)

        userProfiles_list = list(userProfiles) # Make a user profiles list
        userProfiles_list.sort(key = lambda profile: user_list.index(profile.id)) # Sort the user profiles list in the order that user-list is sorted

        userProfilesSerializer = UserProfilesSerializer(userProfiles_list, many = True, context = {'request': request}) # Deserialize the data
        return Response(userProfilesSerializer.data) # Return the response with data


def getProject( pk): # Helper function for get; handles error checking
    try:
        return Projects.objects.get(pk = pk)
    except Projects.DoesNotExist:
        raise Http404
#Helper function for getting a user. Handles error if user doesn't exist. Only used when a UserProfile is supplied as an argument in the URL, else request.user is used to obtain it
def getUser( pk ): 
    try:
        return UserProfiles.objects.get(pk=pk)
    except UserProfiles.DoesNotExist:
        raise Http404


'''
    Returns a list of all projects the requesting user might be interested in based on their preference Types
    stored in the database
'''
class UserSearch(APIView):
    authentication_classes = (TokenAuthentication,)
    permission_classes = (IsAuthenticated,)

    def get(self, request, format = None):
        profile = UserProfiles.objects.get(user_id = request.user.id) # Obtain UserProfile from the requesting User's id 
        types_profiles = Types.user_profiles.through # The types_profiles pivot table
        preferredTypes = types_profiles.objects.filter(userprofiles_id = profile.id) # Obtain all Types preferred by the requesting user
        preferredTypes = [x.types_id for x in preferredTypes] # Put into a list of IDs of preferred types
        
        # If user did not specify a preferred type, return all projects
        if not preferredTypes:
            projects = Projects.objects.all().exclude(owner=profile)
            serializer = ProjectsSerializer(projects, many = True)
            return Response(serializer.data)
        
        types_projects = Types.projects.through # Types_projects pivot table
        rawResults = types_projects.objects.filter(types_id__in=preferredTypes).values('projects_id') # All projects that match preferred types (with duplicate projects) 
        projectIDs = rawResults.annotate(count=Count('projects_id')).order_by('-count') # Get the IDs of the projects in descending order of most "Type" matches
        projectIDs = [x['projects_id'] for x in projectIDs] # Put the IDs into a flat list
        projects = Projects.objects.filter(pk__in = projectIDs) # Get the actual project instances from the IDs
        projects_list = list(projects) # Turn queryset into a list
        projects_list.sort(key = lambda project: projectIDs.index(project.id)) #Sort projects back to proper order based on projectIDs
 
        #Exclude projects that the requesting user has already swiped on, AND projects the user owns
        swiped_projects = Swipes.objects.filter(  Q( user_likes=Swipes.YES) | Q( user_likes=Swipes.NO ), user_profile=profile )
        swiped_project_ids =  [x.project.id for x in swiped_projects]
        owned_projects = Projects.objects.filter( owner=profile )
        projects_list = [x for x in projects_list if (x.id not in swiped_project_ids) and x not in owned_projects ]

        # Deserialize the data and return it
        serializer = ProjectsSerializer(projects_list, many=True, context={'request': request})
        return Response(serializer.data)

'''
    Creates and retrieves a project
'''
class ProjectList(generics.GenericAPIView, mixins.CreateModelMixin):
    authentication_classes = (TokenAuthentication,)
    permission_classes = (IsAuthenticated,)
    queryset = Projects.objects.all()
    serializer_class = ProjectsSerializer
    
    # Override the perform_create function to automatically set the project's owner as the requesting user 
    def perform_create(self, serializer):
        serializer.save(owner = UserProfiles.objects.get(user_id = self.request.user.id))
    
    # Retrieves all projects a user owns
    def get(self, request, *args, **kwargs):
        profile = UserProfiles.objects.get(user_id = request.user.id) # Get the requesting user's profile
        projects = Projects.objects.filter(owner_id = profile.id) # get projects owned by the requesting user
        serializer = ProjectsSerializer(projects, many = True, context = {'request': request}) # deserialize the data
        return Response(serializer.data) # return the requested data
    
    # Creates a new Project with the specified fields
    def post(self, request, *args, **kwargs ):
        request.data['date_created'] = date.today() # Set the date_created field as today's date
        skillsList = request.data.get('skills') # Get a list of all skills associated with this project

        # Check if skills exist in database, create them if they don't. Check for errors after
        if skillsList != None and check_skills(skillsList) == False: 
            return Response( status=status.HTTP_400_BAD_REQUEST ) #TODO: Change to correct code + MORE SPECIFIC DETAILS FOR CLIENT '''
    
        return self.create(request, *args, **kwargs ) # Use CreateModelMixin to create the Project

'''
    Update, Retrieve or Delete an existing project specified by its primary key 
'''
class ProjectDetail(generics.GenericAPIView, mixins.UpdateModelMixin, mixins.RetrieveModelMixin, mixins.DestroyModelMixin):
    authentication_classes = (TokenAuthentication,)
    permission_classes = (IsAuthenticated,)
    queryset = Projects.objects.all()
    serializer_class = ProjectsSerializer

    # Update data for a specific Project
    def put(self, request, *args, **kwargs): 
        project = Projects.objects.get(pk = kwargs['pk'])
        if not isOwner(request, project):
            return Response(status = status.HTTP_403_FORBIDDEN)
        skillsList = request.data.get('skills') #Get a list of all skills associated with this project
        
        #Check if skills exist in database, create them if they don't. Check for errors after
        if skillsList != None and check_skills(skillsList) == False: 
            return Response( status=status.HTTP_400_BAD_REQUEST ) #TODO: Change to correct code + MORE SPECIFIC DETAILS FOR CLIENT '''
        return self.partial_update(request, *args, **kwargs )
    
    # Get data for a specific project
    def get(self, request, *args, **kwargs):
        return self.retrieve(request, *args, **kwargs)
    
    # Delete a specific project
    def delete(self, request, *args, **kwargs):
        project = Projects.objects.get(pk = kwargs['pk'])
        if not isOwner(request, project):
            return Response(status = status.HTTP_403_FORBIDDEN)
        return self.destroy(request, *args, **kwargs)

'''
    Checks if a list of Skills exist in the database and create them if they don't
    @param skillsList - list of skills 
    @postcondition - Return true if skills either all exist OR were successfully created. False if an error occurs when creating a skill
'''
def check_skills(skillsList):
    for skill in skillsList: 
        print skill
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
Inserts the result of a project swiping on a UserProfile into the Swipes table, or updates the entry of it already exists
@requires: 
    In the URL:
        project - a primary key in the url  indicating id of the project that is swiping
        user - a primary key in the url indicating id of the user being swiped on
    In the request Body: 
        project_likes - In the body:  Yes or No indicating if the project likes the user
'''

@api_view(['PUT'])
#@permission_classes( (IsAuthenticated,) )
def project_swipe( request, **kwargs ):
    print "hi"
    #Try to obtain the project and user specified in the URL. 
    project = getProject( kwargs['project'] )
    print "got the project"
    if not isOwner(request, project):
        return Response(status = status.HTTP_403_FORBIDDEN)
    #TODO: IMPLEMENT PERMISSION CHECK TO SEE IF REQUESTING USER OWNS THE PROJECT
    user = getUser( kwargs['user'] ) 
    print "got thet user"
    #Attempt to update the swipe in the Swipes table if it exists
    try:
        swipe = Swipes.objects.get(user_profile=user, project=project )
        serializer = SwipesSerializer( swipe, data=request.data, partial=True ) #update the swipe

        if serializer.is_valid() :
            serializer.save()
            #Send push notification to both users involved if mutual interest is expressed
            if swipe.project_likes == Swipes.YES and swipe.user_likes == Swipes.YES :
                project_owner_device = project.owner.device
                user_device = user.device
                project_owner_device.send_message( "MatchNotification" + " " +  "Project: " + project.project_name + " has found a match!" )
                user_device.send_message("MatchNotification" + " " + "You have found a match!" )
            return Response(serializer.data)

    except Swipes.DoesNotExist: #Create a new entry in swipes table if it doesn't exist
        #Populate request.data with the project and user for creation of new Swipe
        requestData = request.data.copy()
        print "IT DOESN'T EXIST OKAY?"
        requestData['user_profile'] = user.id
        print "got the user profile..... AGAIN"
        requestData['project'] = project.id
        print "got user and project already..."
        serializer = SwipesSerializer(data=requestData)
 
        if serializer.is_valid():
            serializer.save()
            return Response( serializer.data )

    return Response(serializer.errors, status.HTTP_400_BAD_REQUEST ) #If neither an update or create occurred (data was invalid)




'''
    Returns all projects matched with the requesting user
'''
@api_view(['GET'])
def user_matches(request):
    authentication_classes = (TokenAuthentication,)
    permission_classes = (IsAuthenticated,)
    profile = UserProfiles.objects.get(user = request.user) # Get the requesting user's profile
    matched_swipes = Swipes.objects.filter(user_profile=profile, user_likes=Swipes.YES, project_likes=Swipes.YES) # Get all swipes that user is involved in with mutual likes
    #project_list = [matched_swipe.project_id for matched_swipe in matched_swipes]
    #projects = Projects.objects.filter(id__in = project_list)
    #serializer = ProjectsSerializer(projects, many=True, context = {'request': request})
    serializer = UserMatchSerializer(matched_swipes, many = True)
    return Response(serializer.data)

'''
    Returns all users matched with the requesting project owner
'''
@api_view(['GET'])
def project_matches(request):
    authentication_classes = (TokenAuthentication,)
    permission_classes = (IsAuthenticated,)
    
    profile = UserProfiles.objects.get(user = request.user) # Get the requesting user's profile
    projects = Projects.objects.filter(owner = profile) # Get the projects owned by this user
    projectsList = [project.id for project in projects] # Put the project ids into a list
    matched_swipes = Swipes.objects.filter(project_id__in = projectsList, user_likes = Swipes.YES, project_likes = Swipes.YES) # Query the swipes which contain the projects in the project list
    serializer = ProjectMatchSerializer(matched_swipes, many = True) # Deserialize and return the data
    return Response(serializer.data)

'''
    Makes a new user and user profile in the database
'''
class UserList( APIView):

	def post(self, request, *args, **kwargs):
		serializer = UsersSerializer(data = request.data)
		if serializer.is_valid():
			serializer.save()
		else:
			return Response(serializer.errors, status = status.HTTP_400_BAD_REQUEST)

		device = GCMDevice.objects.create(registration_id=request.data.get('device_id')  ) #create new GCMDevice with the given device_id
		user = User.objects.get(username = request.data.get('username'))
		requestData = request.data.copy() # Make a mutable copy of the request
		requestData['user'] = user.id # Set the user field to requesting user
		requestData['device'] = device.id 
		print requestData 
		skillsList = request.data.getlist('skills') # Get a list of all skills associated with this user
		if (skillsList != None) and (not check_skills(skillsList) ): 
			return Response(status = status.HTTP_400_BAD_REQUEST)

		serializer = UserProfilesSerializer(data = requestData)
		if serializer.is_valid():
			serializer.save()
			return Response(serializer.data, status = status.HTTP_201_CREATED)
		return Response(serializer.errors, status = status.HTTP_400_BAD_REQUEST)
	
	def get( self, request, *args, **kwargs ):
		user_profile = UserProfiles.objects.get( user=request.user);
		serializer = UserProfilesSerializer(user_profile);
		return Response( serializer.data );

'''
    Modifies or deletes a user
'''
class UserDetail(APIView):
    authentication_classes = (TokenAuthentication,)
    permission_classes = (IsAuthenticated,)

    # Modifies the profile of the requesting user
    def put(self, request, format = None):
        
        profile = UserProfiles.objects.get(user_id = request.user.id) 
        requestData = request.data.copy() # Make a mutable copy of the request
        requestData['user'] = profile.user_id # Set the user field to requesting user
        
        #Check if user has a new device id. Update it if there is 
        device_id = request.data.get('device_id')
        if device_id != None:
            device = profile.device 
            device.registration_id = device_id;
            device.save();
        
        #print requestData['skills']
        skillsList = request.data.get('skills') # Get a list of all skills associated with this user
        print "Before check skills"
        #If user has submitted skills, check if the skills exist in the database, create them if they don't
        if (skillsList != None) and ( not check_skills(skillsList) ): 
            return Response(status = status.HTTP_400_BAD_REQUEST)
        print "After"
        serializer = UserProfilesSerializer(profile, data = requestData, partial=True)
        if serializer.is_valid():
            serializer.save()
            print serializer.data
            return Response(serializer.data)
        return Response(serializer.errors, status = status.HTTP_400_BAD_REQUEST)

    # Deletes a user and the corresponding user profile
    def delete(self, request, format = None):
        user = User.objects.get(id = request.user.id) # Get the instance of the requesting user and the user profile
        profile = UserProfiles.objects.get(user_id = user.id)
        user.delete() # Delete both the user and the user profile
        profile.delete()
        return Response(status = status.HTTP_204_NO_CONTENT)
'''
    Updates a Swipes row every time a user swipes on a project
'''
class UserSwipe(APIView):
    authentication_classes = (TokenAuthentication,)
    permission_classes = (IsAuthenticated,)

    def put(self, request, pk, format = None):
        profile = UserProfiles.objects.get(user_id = request.user.id)
        project = Projects.objects.get(pk = pk)
        requestData = request.data.copy() # Make a mutable copy of the request
        requestData['user_profile'] = profile.id # Set the user field to requesting user
        requestData['project'] = project.id

        try:
            swipe = Swipes.objects.get(user_profile = profile, project = project)
            serializer = SwipesSerializer(swipe, data = requestData)
            if serializer.is_valid():
                serializer.save()
                swipe = Swipes.objects.get(user_profile = profile, project = project)
                if swipe.user_likes == Swipes.YES and swipe.project_likes == Swipes.YES:
                    project_owner_device = project.owner.device # Get Device ID of project owner
                    user_device = profile.device # Get GCM device with requesting user's device ID 

                    project_owner_device.send_message( "MatchNotification" + " " + "Project: " + project.project_name + " has found a match!")
                    user_device.send_message("MatchNotification" + " " + "You have found a match!")
                return Response(serializer.data)
            return Response(serializer.errors, status = status.HTTP_400_BAD_REQUEST)

        except Swipes.DoesNotExist:
            serializer = SwipesSerializer(data = requestData)
            if serializer.is_valid():
                serializer.save()
                return Response(serializer.data)
            return Response(serializer.errors, status = status.HTTP_400_BAD_REQUEST)

class Chat(APIView): 
    authentication_classes = (TokenAuthentication,)
    permission_classes = (IsAuthenticated,)
    
    def post(self, request, format = None):  
        requestData = request.data.copy()
        profile = UserProfiles.objects.get(user_id = request.user.id)
        requestData['sender'] = profile.id
	requestData['time_sent'] = str(datetime.now())
        recipient = UserProfiles.objects.get(id = request.data['recipient'])
        recipient_device = recipient.device
        recipient_device.send_message("ChatNotification" + " " + request.data['message'], extra = {'sender': str(profile.id), 'recipient': str(recipient.id), 'time_sent': str(datetime.now())})
	# recipient_device.send_message(None, extra = {'sender': str(profile.id), 'recipient': str(recipient.id), 'time_sent': str(datetime.now())})
        return Response(requestData, status = status.HTTP_201_CREATED)
        '''
            serializer = ChatSerializer(data = requestData)
            if serializer.is_valid():
                serializer.save()
                return Response(serializer.data, status = status.HTTP_201_CREATED)
            return Response(serializer.errors, status = status.HTTP_400_BAD_REQUEST)
        '''

class UserGet(APIView): 
    authentication_classes = (TokenAuthentication,)
    permission_classes = (IsAuthenticated,)
    def get_object(self, pk):
        try:
            return UserProfiles.objects.get(pk=pk)
        except Snippet.DoesNotExist:
            raise Http404

    def get(self, request, pk, format=None):
        profile = self.get_object(pk)
        serializer = UserProfilesSerializer(profile)
        return Response(serializer.data)



@api_view(['GET'])
def skills(request):
    all_skills = Skills.objects.all()
    serializer = SkillsSerializer( all_skills, many=True )
    return Response( data = serializer.data )

class UserImageList(APIView):
    authentication_classes = (TokenAuthentication,)
    permission_classes = (IsAuthenticated,)

    def get(self, request, format = None):
        image = UserImages.objects.get()



def isOwner(request, project):
    return request.user == project.owner.user
