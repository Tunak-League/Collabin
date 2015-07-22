from django.conf.urls import url
from server import views
from rest_frameswork.urlpatterns import format_suffix_patterns


# API endpoints
urlpatterns = [
    url(r'^$', views.api_root),
	url(r'^server/$',
        views.UserList.as_view(),
        name='user-list'),
    url(r'^server/(?P<pk>[0-9]+)/$',
        views.UserDetail.as_view(),
        name='User-detail'),
]

# Login and logout views for the browsable API
urlpatterns += [
    url(r'^api-auth/', include('rest_framework.urls',
                               namespace='rest_framework')),
]
