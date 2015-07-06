from django.db import models

# Create your models here.
class Users(models.Model):
	last_name = models.CharField(max_length=30)
	first_name = models.CharField(max_length=30)
	email = models.CharField(max_length=30)
	password = models.CharField(max_length=20)
	user_summary = models.CharField(max_length=500)
	location = models.CharField(max_length=30)

class Skills(models.Model):
	skill_name = models.CharField(max_length=30)

class Types(models.Model):
	type_name = models.CharField(max_length=30)

