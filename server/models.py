from django.db import models

class Users(models.Model):
    last_name = models.CharField(max_length=30)
    first_name = models.CharField(max_length=30)
    email = models.CharField(max_length=30)
    password = models.CharField(max_length=20)
    user_summary = models.CharField(max_length=500)
    location = models.CharField(max_length=30)
    image_path = models.CharField(max_length=50, null = True, blank = True, default = None)

class Skills(models.Model):
    skill_name = models.CharField(max_length=30)
    users = models.ManyToManyField('Users')
    projects = models.ManyToManyField('Projects')

class Types(models.Model):
    type_name = models.CharField(max_length=30)
    users = models.ManyToManyField('Users')

class Projects(models.Model):
    project_name = models.CharField(max_length=30)
    project_summary = models.CharField(max_length=500)
    date_created = models.DateField()
    id_types = models.ForeignKey('Types')
    id_owner = models.ForeignKey('Users')
    image_path = models.CharField(max_length=50, null = True, blank = True, default = None)
