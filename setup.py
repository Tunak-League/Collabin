from django.contrib.auth.models import User
from server.models import  Types, Skills, Projects, UserProfiles
import datetime

daler=UserProfiles.objects.create(user_id=1, user_summary="God of the Universe", location="Tunak Desert" )
justin=UserProfiles.objects.create(user_id=2, user_summary="Disciple of Daler", location="Vancouver" )

software = Types.objects.create( type_name="Software" )
electrical = Types.objects.create( type_name="Electrical" )
mechanical = Types.objects.create( type_name="Mechanical" )
business = Types.objects.create( type_name="Business" )



daler.types.add(software)
daler.types.add(electrical)

from django.contrib.auth.models import User
from server.models import  Types, Skills, Projects, UserProfiles

daler=UserProfiles.objects.create(user_id=5, user_summary="God of the Universe", location="Tunak Desert" )
justin=UserProfiles.objects.create(user_id=6, user_summary="Disciple of Daler", location="Vancouver" )

software = Types.objects.create( type_name="Software" )
electrical = Types.objects.create( type_name="Electrical" )
mechanical = Types.objects.create( type_name="Mechanical" )
business = Types.objects.create( type_name="Business" )



daler.types.add(software)
daler.types.add(electrical)

import datetime
a=Projects.objects.create(project_name = "a", project_summary = "aaa", id_owner_id=4,  date_created=datetime.date.today() )

b=Projects.objects.create(project_name = "b", project_summary = "aaa", id_owner_id=4 , date_created=datetime.date.today() )


c=Projects.objects.create(project_name = "c", project_summary = "aaa",  id_owner_id=4, date_created=datetime.date.today() )


d=Projects.objects.create(project_name = "d", project_summary = "aaa" , id_owner_id=4, date_created=datetime.date.today() )


e=Projects.objects.create(project_name = "e", project_summary = "aaa",  id_owner_id=4, date_created=datetime.date.today() )


f=Projects.objects.create(project_name = "f", project_summary = "aaa",  id_owner_id=4, date_created=datetime.date.today() )
                                                                                                                                             
a.types.add(1)
b.types.add(2)
c.types.add(3)
d.types.add(1,2)
e.types.add(4)
f.types.add(1,2)
