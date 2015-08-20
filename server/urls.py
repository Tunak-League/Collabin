from django.conf.urls import url
from rest_framework.urlpatterns import format_suffix_patterns
from server import views

urlpatterns = format_suffix_patterns([
    url(r'^project-search/(?P<pk>[0-9]+)/$', views.ProjectSearch.as_view(), name='project-search'),
    url(r'^project-list/', views.ProjectList.as_view(), name='project-list'),
    url(r'^project-detail/(?P<pk>[0-9]+)/$', views.ProjectDetail.as_view(), name='project-detail'),
    url(r'^user-search/$', views.UserSearch.as_view()),
    url(r'^project-matches/', views.project_matches, name='project-matches'),
    url(r'^user-swipe/(?P<pk>[0-9]+)/$', views.UserSwipe.as_view()),
    url(r'^user-list', views.UserList.as_view() ),
    url(r'^user-detail', views.UserDetail.as_view()),
    url(r'^project-swipe/(?P<project>[0-9]+)/(?P<user>[0-9]+)/$', views.project_swipe, name="project-swipe"),
    url(r'^chat/$', views.Chat.as_view()),
    url(r'^user-get/(?P<pk>[0-9]+)/$', views.UserGet.as_view()),
    url(r'^user-matches/', views.user_matches),
    url(r'^skills/$', views.skills ),
])

