from server.models import Projects
from server.models import Types
import datetime
#Make a bunch of projects owned by justinly. Each project must have a type
#Projects.objects.create(project_name = "a", project_summary = "aaa", id_types_id=1, id_owner_id=3 , date_created=datetime.date.today() )

Projects.objects.create(project_name = "b", project_summary = "aaa", id_types_id=1, id_owner_id=3 , date_created=datetime.date.today() )


Projects.objects.create(project_name = "c", project_summary = "aaa", id_types_id=2, id_owner_id=3 , date_created=datetime.date.today() )


Projects.objects.create(project_name = "d", project_summary = "aaa", id_types_id=3, id_owner_id=3 , date_created=datetime.date.today() )


Projects.objects.create(project_name = "e", project_summary = "aaa", id_types_id=4, id_owner_id=3 , date_created=datetime.date.today() )
