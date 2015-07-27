from django.conf.urls import url
from rest_framework.urlpatterns import format_suffix_patterns
from server import views

urlpatterns = [
    url(r'^server/$', views.Users.as_view()),
    url(r'^server/(?P<pk>[0-9]+)/$', views.UserDetail.as_view()),
]

urlpatterns = format_suffix_patterns(urlpatterns)
