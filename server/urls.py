from django.conf.urls import url
from rest_framework.urlpatterns import format_suffix_patterns
from server import views

urlpatterns = format_suffix_patterns([
    url(r'^project-search/', views.ProjectSearch.as_view(), name='project-search'),
    url(r'^project-list/', views.ProjectList.as_view(), name='project-list'),
    url(r'^project-detail/(?P<pk>[0-9]+)/$', views.ProjectDetail.as_view(), name='project-detail'),
    url(r'^user-search/(?P<pk>[0-9]+)/$', views.UserSearch.as_view()),
    url(r'^project-matches/', views.project_matches, name='project-matches'),
])

