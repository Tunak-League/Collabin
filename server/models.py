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
    user_image = models.ImageField(upload_to='users', null = True, blank = True, default=None)
    user = models.ForeignKey(settings.AUTH_USER_MODEL)
    device = models.ForeignKey('push_notifications.GCMDevice', default = None)
    
    def save(self, *args, **kwargs):
        try:
            this = UserProfiles.objects.get(id=self.id)
            if this.user_image != self.user_image:
                this.user_image.delete(save=False)
        except: pass # when new photo then we do nothing, normal case  	    
        super(UserProfiles, self).save(*args, **kwargs)

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
    project_image = models.ImageField(upload_to='projects', null=True, blank=True, default=None)

    def save(self, *args, **kwargs):
        try:
            this = Projects.objects.get(id=self.id)
            if this.project_image != self.project_image:
                this.project_image.delete(save=False)
        except: pass # when new photo then we do nothing, normal case  	    
        super(Projects, self).save(*args, **kwargs)

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
