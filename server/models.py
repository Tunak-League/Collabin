from django.db import models
from django.contrib.auth.models import User
from django.conf import settings

class UserProfiles(models.Model):
    user_summary = models.CharField(max_length=500)
    location = models.CharField(max_length=30)
    image_path = models.CharField(max_length=50, null = True, blank = True, default = None)
    user = models.ForeignKey(settings.AUTH_USER_MODEL)	

class Skills(models.Model):
    skill_name = models.CharField(max_length=30)
    users = models.ManyToManyField('UserProfiles', related_name="skills")
    projects = models.ManyToManyField('Projects', related_name="skills")

class Types(models.Model):
    type_name = models.CharField(max_length=30)
    users = models.ManyToManyField('UserProfiles', related_name="types")

class Projects(models.Model):
    project_name = models.CharField(max_length=30)
    project_summary = models.CharField(max_length=500)
    date_created = models.DateField()
    id_types = models.ForeignKey('Types')
    id_owner = models.ForeignKey('UserProfiles')
    image_path = models.CharField(max_length=50, null = True, blank = True, default = None)

from django.conf import settings 
from django.db.models.signals import post_save 
from django.dispatch import receiver 
from rest_framework.authtoken.models import Token 
 
@receiver(post_save, sender=settings.AUTH_USER_MODEL) 
def create_auth_token(sender, instance=None, created=False, **kwargs): 
    if created: 
        Token.objects.create(user=instance) 
