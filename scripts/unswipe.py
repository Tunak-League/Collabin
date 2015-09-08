from django.contrib.auth.models import User
from server.models import  Types, Skills, Projects, UserProfiles, Swipes
import datetime

Swipes.objects.all().delete()
