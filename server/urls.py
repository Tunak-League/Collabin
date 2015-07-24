from django.conf.urls import url
from server import views
from rest_frameswork.urlpatterns import format_suffix_patterns


# API endpoints
urlpatterns = ([
    url(r'^$', views.api_root),
    url(r'^server/$', views.UserList.as_view(), name='user-list'),
    url(r'^server/(?P<pk>[0-9]+)/$', views.UserDetail.as_view(), name='user-detail'),

    url(r'^server/$', views.SkillsList.as_view(), name='skills-list'),
    url(r'^server/(?P<pk>[0-9]+)/$', views.SkillsDetail.as_view(), name='skills-detail'),

    url(r'^server/$', views.TypesList.as_view(), name='types-list'),
    url(r'^server/(?P<pk>[0-9]+)/$', views.TypesDetail.as_view(), name='types-detail'),

    url(r'^server/$', views.ProjectsList.as_view(), name='projects-list'),
    url(r'^server/(?P<pk>[0-9]+)/$', views.ProjectsDetail.as_view(), name='projects-detail'),
    

])

# Login and logout views for the browsable API
urlpatterns += [
    url(r'^api-auth/', include('rest_framework.urls',
                               namespace='rest_framework')),
]
