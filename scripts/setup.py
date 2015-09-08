from django.contrib.auth.models import User
from server.models import  Types, Skills, Projects, UserProfiles
import datetime
from push_notifications.models import APNSDevice, GCMDevice

daler=User.objects.create_user(username="daler", password="tunak", email="daler@gmail.com" )
justin=User.objects.create_user(username="justin", password="tunak", email="justin@gmail.com" )
bob=User.objects.create_user(username="bob", password="tunak", email="bob@gmail.com" )

device1 = GCMDevice.objects.create( registration_id="bs1")
device2 = GCMDevice.objects.create( registration_id="bs2" )
device3 = GCMDevice.objects.create( registration_id="bs3" )

daler=UserProfiles.objects.create(user=daler, user_summary="God of the Universe", location="Tunak Desert", device=device1 )
justin=UserProfiles.objects.create(user=justin, user_summary="Disciple of Daler", location="Vancouver", device=device2 )
bob = UserProfiles.objects.create(user=bob, user_summary="some guy", location = "Burnaby", device=device3 )

software = Types.objects.create( type_name="Software" )
electrical = Types.objects.create( type_name="Electrical" )
mechanical = Types.objects.create( type_name="Mechanical" )
business = Types.objects.create( type_name="Business" )



daler.types.add(software)
daler.types.add(electrical)

justin.types.add(software)

bob.types.add(business, mechanical)

import datetime
a=Projects.objects.create(project_name = "a", project_summary = "aaa", owner=justin,  date_created=datetime.date.today() )

b=Projects.objects.create(project_name = "b", project_summary = "aaa", owner=justin , date_created=datetime.date.today() )


c=Projects.objects.create(project_name = "c", project_summary = "aaa",  owner=justin, date_created=datetime.date.today() )


d=Projects.objects.create(project_name = "d", project_summary = "aaa" , owner=justin, date_created=datetime.date.today() )


e=Projects.objects.create(project_name = "e", project_summary = "aaa",  owner=justin, date_created=datetime.date.today() )


f=Projects.objects.create(project_name = "f", project_summary = "aaa",  owner=justin, date_created=datetime.date.today() )
                                                                                                                                             
a.types.add(software)
b.types.add(electrical)
c.types.add(mechanical)
d.types.add(software, electrical)
e.types.add(business)
f.types.add(software, electrical)

##CREATE SKILLS

java = Skills.objects.create(skill_name="Java")
c = Skills.objects.create(skill_name="C")
html = Skills.objects.create(skill_name="HTML")
php = Skills.objects.create(skill_name="PHP")
python = Skills.objects.create(skill_name="Python")


#PREFERRED SKILLS
a.skills.add(java, html, python )


#Assigning Skills to Users
justin.skills.add(java, html, python )
daler.skills.add(java, html, php, c )
bob.skills.add(html, php )
