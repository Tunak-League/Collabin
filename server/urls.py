from django.conf.urls import url
from server import views

urlpatterns = format_suffix_patterns([
    url(r'^project-search/', views.ProjectSearch.as_view(), name='project-search' ),
    url(r'^project/', views.Project.as_view(), name='project'),
    url(r'^user-search/(?P<pk>[0-9]+)/$', views.UserSearch.as_view()),
])

