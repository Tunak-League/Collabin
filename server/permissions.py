from rest_framework import permissions
from server.models import UserProfiles, Projects

class IsOwnerOrReadOnly(permissions.BasePermission):
    '''
    Permission that only allows the owner of a Project to edit it. Non-owners can only read
    @requires: obj is a Project object
    '''

    def has_object_permissions(self, request, view, obj):
        #Allow GET requests
        if request.method in permissions.SAFE_METHODS:
            return True

        return request.user == obj.owner
