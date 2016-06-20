# Authorization Management API

The `AuthorizationManager` is the main interface for dealing with the authorization management API. The following is an example of how to check if the read perspective permission has been granted to an specific user:

```
@Inject
AuthorizationManager authzManager;

Perspective perpsective1;
User user;
...
boolean result = authzManager.authorize(perspective1, user);
```

We do need to inject the _AuthorizationManager_ instance and then use its different methods to check different permissions. In this example, no resource action is passed to the `authorize` call so that means the system will check if the specified user can "access" a given resource. The term "access" refers to the ability to be able to reach, read or view a resource. For instance, read a file, view an item in the UI, etc.

Notice that, by default, if a permission is not defined in the authorization policy it is granted by default to everyone. In order to protect access to a given perspective we need to make sure the required entries are added to the policy. For example:

```
# Admin users can read all
role.admin.permission.perspective.read=true

# Manager users can read only a few perspectives
role.manager.permission.perspective.read=false
role.manager.permission.perspective.read.perspective1=true
```

Calls to _AuthorizationManager_ make use of the permission entries stored in the security policy to determine whether access is granted to the user. The permissions collected are those assigned to any of the roles and groups the user belongs to. In case the same permission is assigned to different roles/groups the system will apply a conflict resolution algorithm (see the [_Permission Resolution_](Permission Resolution) section to figure out the details).


