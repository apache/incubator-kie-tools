# Security

Uberfire provides a complete authorization management subsystem for protecting user access to specific resources or features. In order to understand how the security mechanism works a few core concepts need to be introduced first.

#### Roles and groups

Users can be assigned with more than one role and/or group. It is always mandatory to assign at least one role to the user, otherwise he/she won’t be able to login. Roles are defined at app. server level and they are part of the webapp’s web.xml descriptor. On the other hand, groups are a more flexible concept, since they can be defined at runtime.

#### Permissions

A permission is basically something the user can do within the application. Usually, an action related to a specific resource. For instance:

* View a perspective
* Save a project
* View a repository
* Delete a dashboard

A permission can be granted or denied and it can be global or resource specific. For instance:

* Global   => “Create new perspectives”
* Specific => “View home perspective”

As you can see, a permission is a `resource + action` pair. In the concrete case of a perspective we have: _save, delete, rename &amp; copy_ as the available actions. That means that there exist 4 possible permissions that could be granted for perspectives.

Permissions do not necessarily need to be tied to a resource. Sometimes it is also neccessary to protect access to specific features, like for instance "_generate a sales report_". That means, permissions can be used not only to protect access to resources but also to custom features within the application.

#### Authorization policy

The set of permissions assigned to every role and/or group is what's called the authorization (or security) policy. Every Uberfire application contains a single security policy which is used every time the system is checking a permission.

The authorization policy file is stored in a file called _WEB-INF/classes/security-policy.properties_ stored under the application's WAR structure.

```
NOTE: If no policy is defined then the authorization management features are disabled
and Uberfire takes that as if all the resources & features were granted by default.

```
Here is an example of a security policy file:

```
# Role "admin"
role.admin.permission.perspective.read=true
role.admin.permission.perspective.read.Dashboard=false

# Role "user"
role.user.permission.perspective.read=false
role.user.permission.perspective.read.Home=true
role.user.permission.perspective.read.Dashboard=true

```

As you can see every entry defines a single permission which is assigned to a role/group. On application start up, the policy file is loaded and stored into memory. 

#### Security checks

The _AuthorizationManager_ is the main interface for checking if a permission is granted to the user.

```
@Inject
AuthorizationManager authzManager;

Perspective perpsective1;
User user;
...
boolean result = authzManager.authorize(perspective1, user);
```

The security check calls always use the permissions defined in the security policy.

