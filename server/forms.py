from rest_framework.authtoken.models import Token 
from django.contrib.auth.models import User
from server import models
from django.forms import ModelForm
from django.core.exceptions import NON_FIELD_ERRORS

class UserForm(ModelForm):
	class Meta:
		model = User
		fields = ['username','password','email','first_name','last_name']
		error_messages = {
			NON_FIELD_ERRORS: {
				'unique_together': "%(User)s's %(username)s is already taken, please enter a different user name.",
				'unique_together': "%(User)s's %(email)s is already taken, please enter a different email address.",
			}
		}


class ProjectsForm(ModelForm):
	class Meta:
		model = Projects
		fields = ['project_name','project_summary','date_created','owner','image_path']
		error_messages = {
			NON_FIELD_ERRORS: {
				'unique_together': "%(Projects)s's %(project_name)s is taken, please enter a different project name.",
			}
		}
