from django.forms import widgets
from rest_framework import serializers
from server.models import UserProfiles, Skills, Types, Projects, Swipes
from django.contrib.auth.models import User

class Base64ImageField(serializers.ImageField):
    """
    A Django REST framework field for handling image-uploads through raw post data.
    It uses base64 for encoding and decoding the contents of the file.

    Heavily based on
    https://github.com/tomchristie/django-rest-framework/pull/1268

    Updated for Django REST framework 3.
    """

    def to_internal_value(self, data):
        from django.core.files.base import ContentFile
        import base64
        import six
        import uuid

        # Check if this is a base64 string
        if isinstance(data, six.string_types):
            # Check if the base64 string is in the "data:" format
            if 'data:' in data and ';base64,' in data:
                # Break out the header from the base64 content
                header, data = data.split(';base64,')

            # Try to decode the file. Return validation error if it fails.
            try:
                decoded_file = base64.b64decode(data)
            except TypeError:
                self.fail('invalid_image')

            # Generate file name:
            file_name = str(uuid.uuid4())[:12] # 12 characters are more than enough.
            # Get the file name extension:
            file_extension = self.get_file_extension(file_name, decoded_file)

            complete_file_name = "%s.%s" % (file_name, file_extension, )

            data = ContentFile(decoded_file, name=complete_file_name)

        return super(Base64ImageField, self).to_internal_value(data)

    def get_file_extension(self, file_name, decoded_file):
        import imghdr

        extension = imghdr.what(file_name, decoded_file)
        extension = "jpg" if extension == "jpeg" else extension

        return extension

class UsersSerializer(serializers.ModelSerializer):
    username = serializers.CharField(error_messages = {'required': 'Please enter a username',})
    email = serializers.CharField(required = True, error_messages = {'required': 'Please enter your email address',})  
    password = serializers.CharField(required = True, error_messages = {'required': 'Please enter a password'})

    def create(self, validated_data):
        password = validated_data.pop('password', None)
        instance = self.Meta.model(**validated_data)
        if password is not None:
            instance.set_password(password)
        instance.save()
        return instance

    def update(self, instance, validated_data):
        for attr, value in validated_data.items():
            if attr == 'password':
                instance.set_password(value)
            else:
                setattr(instance, attr, value)
        instance.save()
        return instance
    
    def validate_username(self, value):
        try:
            User.objects.get(username = value)
            raise serializers.ValidationError('The username you specified is already taken. Please enter a different username')
        except User.DoesNotExist:
            return value

    def validate_email(self, value):
        try:
            User.objects.get(email = value)
            raise serializers.ValidationError('The email you specified is already in use. Please enter a different email address')
        except User.DoesNotExist:
            return value
    
    class Meta:
        model = User
        fields = ('id', 'first_name', 'last_name', 'username', 'password', 'email')
        

class UserProfilesSerializer(serializers.ModelSerializer):
    skills = serializers.SlugRelatedField (
        many = True,
        queryset = Skills.objects.all(),
        slug_field = 'skill_name',
    )
    types = serializers.SlugRelatedField (
        many = True,
        queryset = Types.objects.all(),
        slug_field = 'type_name'
    )
    last_name = serializers.ReadOnlyField(source = 'user.last_name')
    first_name= serializers.ReadOnlyField(source = 'user.first_name')
    email = serializers.ReadOnlyField(source = 'user.email')
    username = serializers.ReadOnlyField(source = 'user.username')
    user_image = Base64ImageField( max_length=None, use_url=True, required=False ) 

    class Meta:
        model = UserProfiles
        fields = ('id', 'last_name', 'first_name', 'email', 'username', 'user_summary', 'location',  'skills', 'types', 'user', 'device', 'user_image')

class SkillsSerializer(serializers.ModelSerializer):
    #users = serializers.PrimaryKeyRelatedField(many = True, read_only = True)
    #projects = serializers.PrimaryKeyRelatedField(many = True, read_only = True)
    class Meta:
        model = Skills
        fields = ('id', 'skill_name')

class TypesSerializer(serializers.ModelSerializer):
    users = serializers.PrimaryKeyRelatedField(many = True, read_only = True)
    class Meta:
        model = Types
        fields = ('id', 'type_name', 'users')

class ProjectsSerializer(serializers.ModelSerializer):
    types = serializers.SlugRelatedField (
        many = True,
        queryset = Types.objects.all(),
        slug_field = 'type_name',
    )
    skills = serializers.SlugRelatedField (
        many = True,
        queryset = Skills.objects.all(),
        slug_field = 'skill_name',
    )
    owner_name = serializers.ReadOnlyField(source='owner.user.username')
    project_name = serializers.CharField(error_messages = {'required': 'Please enter a name for your project',})
    project_image = Base64ImageField( max_length=None, use_url=True , required=False) 

    def validate_project_name(self, value):
        try:
            Projects.objects.get(project_name = value)
            raise serializers.ValidationError('Another project with the name you specified exists. Please enter a different project name')
        except Projects.DoesNotExist:
            return value

    class Meta:
        model = Projects
        fields = ('id', 'project_name', 'project_summary', 'owner', 'date_created', 'types', 'skills', 'owner_name', 'project_image' )
        read_only_fields = ('owner',)

class SwipesSerializer(serializers.ModelSerializer):
    class Meta:
        model = Swipes
        fields = ('id', 'user_profile', 'project', 'user_likes', 'project_likes')

class ProjectMatchSerializer( serializers.ModelSerializer ):
    username = serializers.ReadOnlyField(source='user_profile.user.username')
    userProfileId = serializers.ReadOnlyField(source = 'user_profile.id')
    ownerId = serializers.ReadOnlyField(source = 'project.owner.id')
    project_name = serializers.ReadOnlyField(source='project.project_name')
    projectId = serializers.ReadOnlyField(source='project.id')
    class Meta:
        model = Swipes
        fields = ('user_profile', 'userProfileId', 'username', 'project_name', 'ownerId', 'projectId')

class UserMatchSerializer(serializers.ModelSerializer):
    project_name = serializers.ReadOnlyField( source='project.project_name' )
    owner = serializers.ReadOnlyField( source='project.owner.user.username')
    userProfileId = serializers.ReadOnlyField(source = 'user_profile.id')
    ownerId = serializers.ReadOnlyField(source = 'project.owner.id')
    projectId = serializers.ReadOnlyField(source='project.id')
    class Meta:
        model = Swipes
        fields = ('project', 'project_name', 'owner', 'ownerId', 'userProfileId', 'projectId') 


