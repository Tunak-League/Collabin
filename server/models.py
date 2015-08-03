from django.db import models
from django.db.models.signals import post_save 
from django.conf import settings
from django.contrib.auth.models import User
from django.dispatch import receiver 
from rest_framework.authtoken.models import Token 
from push_notifications.models import  GCMDevice

class UserProfiles(models.Model):
    user_summary = models.CharField(max_length = 500, null=True, blank=True, default=None)
    location = models.CharField(max_length = 30, null=True, blank=True, default=None)
    image_path = models.CharField(max_length = 50, null = True, blank = True, default = None)
    user = models.ForeignKey(settings.AUTH_USER_MODEL)
    device = models.ForeignKey( 'push_notifications.GCMDevice' )

class Skills(models.Model):
    skill_name = models.CharField(max_length = 30, unique = True)
    user_profiles = models.ManyToManyField('UserProfiles', related_name = "skills")
    projects = models.ManyToManyField('Projects', related_name = "skills")

class Types(models.Model):
    type_name = models.CharField(max_length = 30, unique = True)
    user_profiles = models.ManyToManyField('UserProfiles', related_name = "types")
    projects = models.ManyToManyField('Projects', related_name = "types")

class Projects(models.Model):
    project_name = models.CharField(max_length = 30, unique = True)
    project_summary = models.CharField(max_length = 500)
    date_created = models.DateField(null = True, blank = True, default = None)
    owner = models.ForeignKey('UserProfiles')
    image_path = models.CharField(max_length = 50, null = True, blank = True, default = None)

class Swipes(models.Model):
    YES = 'YES'
    NO = 'NO'

    SWIPE_CHOICES = (
        (YES, 'YES'),
        (NO, 'NO'),
    )

    user_profile = models.ForeignKey('UserProfiles')
    project = models.ForeignKey('Projects')
    user_likes = models.CharField(max_length = 3, null = True, blank = True, default = None, choices = SWIPE_CHOICES)
    project_likes = models.CharField(max_length = 3, null = True, blank = True, default = None, choices = SWIPE_CHOICES)

@receiver(post_save, sender=settings.AUTH_USER_MODEL) 
def create_auth_token(sender, instance = None, created = False, **kwargs): 
    if created: 
        Token.objects.create(user = instance) 
