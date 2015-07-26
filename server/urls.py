
from django.conf.urls import url, include
from server import views
from rest_framework.urlpatterns import format_suffix_patterns


# API endpoints
urlpatterns = format_suffix_patterns([
    url(r'^$', views.api_root),
    url(r'^user-search/$', views.UserSearch.as_view(), name='user-search'),
    url(r'^user/(?P<pk>[0-9]+)/$', views.UserDetail.as_view(), name='user'),

    

])
