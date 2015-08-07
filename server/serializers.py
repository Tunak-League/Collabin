from django.forms import widgets
from rest_framework import serializers
from server.models import UserProfiles, Skills, Types, Projects, Swipes
from django.contrib.auth.models import User

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
    class Meta:
        model = UserProfiles
        fields = ('id', 'last_name', 'first_name', 'email', 'username', 'user_summary', 'location', 'image_path', 'skills', 'types', 'user', 'device')

class SkillsSerializer(serializers.ModelSerializer):
    users = serializers.PrimaryKeyRelatedField(many = True, read_only = True)
    projects = serializers.PrimaryKeyRelatedField(many = True, read_only = True)
    class Meta:
        model = Skills
        fields = ('id', 'skill_name', 'users', 'projects')

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

    def validate_project_name(self, value):
        try:
            Projects.objects.get(project_name = value)
            raise serializers.ValidationError('Another project with the name you specified exists. Please enter a different project name')
        except Projects.DoesNotExist:
            return value

    class Meta:
        model = Projects
        fields = ('id', 'project_name', 'project_summary', 'owner', 'date_created', 'image_path','types', 'skills', 'owner_name')
        read_only_fields = ('owner',)

class SwipesSerializer(serializers.ModelSerializer):
    class Meta:
        model = Swipes
        fields = ('id', 'user_profile', 'project', 'user_likes', 'project_likes')

class ProjectMatchSerializer( serializers.ModelSerializer ):
    project_name = serializers.ReadOnlyField( source='project.project_name' )
    owner = serializers.ReadOnlyField( source='project.owner.user.username')
    class Meta:
        model = Swipes
        fields = ('project', 'project_name', 'owner' ) 

class UserMatchSerializer( serializers.ModelSerializer ):
    username = serializers.ReadOnlyField( source='user_profile.user.username' )
    project_name = serializers.ReadOnlyField(source='project.project_name')
    class Meta:
        model = Swipes
        fields = ('user_profile', 'username', 'project_name')
