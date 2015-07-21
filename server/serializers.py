from django.forms import widgets
from rest_framework import serializers
from server.models import UserProfiles, Skills, Types, Projects
from django.contrib.auth.models import User

class UsersSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = User
        fields = ('url', 'first_name', 'last_name', 'username', 'password', 'user_permissions')

class UserProfilesSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = UserProfiles
        fields = ('url', 'user_summary', 'location', 'image_path')

class SkillsSerializer(serializers.HyperlinkedModelSerializer):
    users = serializers.HyperlinkedRelatedField(many = True, view_name = '', read_only=True)
    projects = serializers.HyperlinkedRelatedField(many = True, view_name = '', read_only=True)
    class Meta:
        model = Skills
        fields = ('url', 'skill_name', 'users', 'projects')

class TypesSerializer(serializers.HyperlinkedModelSerializer):
    users = serializers.HyperlinkedRelatedField(many = True, view_name = '', read_only=True)
    class Meta:
        model = Types
        fields = ('url', 'type_name', 'users')

class ProjectsSerializer(serializers.HyperlinkedModelSerializer):
  #  owner = serializers.HyperlinkedRelatedField(many = False, view_name = '', read_only=True) #NO VIEW RIGHT NOW, COMMENT OUT UNTIL THEN
   # url = serializers.HyperlinkedIdentityField(view_name="project-list"  )
    types = serializers.ReadOnlyField(source = 'id_types.type_name')
    class Meta:
        model = Projects
        fields = (   'project_name', 'project_summary', 'date_created', 'image_path','types' ) # 'owner')
