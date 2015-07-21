from django.conf.urls import url
from server import views

urlpatterns = [
    #url(r'^$', views.index, name = 'index')
    url(r'^project-search/', views.ProjectSearch.as_view(), name='project-search' ),
    url(r'^project/', views.Project.as_view(), name='project' ),
]

