from django.conf.urls import url
from server import views

urlpatterns = [
    #url(r'^$', views.index, name = 'index')
    url(r'^project-list/', views.ProjectList.as_view(), name='project-list' ),
]

