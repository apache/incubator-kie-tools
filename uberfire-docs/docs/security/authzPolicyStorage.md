# Authorization Policy Storage

The authorization policy file is stored in a file called _WEB-INF/classes/security-policy.properties_ stored under the application's WAR structure.

Here is an example of a security policy:

```
# Role "admin"
role.admin.home=Home
role.admin.priority=1
role.admin.permission.perspective.read=true
role.admin.permission.perspective.read.Dashboard=false

# Role "user"
role.user.home=Dashboard
role.user.priority=0
role.user.permission.perspective.read=false
role.user.permission.perspective.read.Home=true
role.user.permission.perspective.read.Dashboard=true
```

The format of the key/value pairs is:

`classifier.identifier.setting.extra=value`

Where:

* `classifier` = role|group
* `identifier` = An existing role or group identifier (depending on the classifier type)
* `setting` = home|priority|permission
* `extra` = Extra setting information. Mandatory for instance to define the permission's name
* `value` = The setting value (depends on the setting selected). Value expected per setting type:
    * `home`: An existing perspective identifier to redirect after login
    * `priority`: An integer indicating how priority is this role|group compared to others. Used for conflict resolution.
    * `permission`: A name representing a specific feature or capability over a given resource.


Permissions, home perspectives and role priorities are all defined in the same file. In some cases though, it might be appropriate to split the policy into several files, basically for two main reasons:

* Separation of concerns
* Maintainability

The main policy can be split into several `security-module` files each of them contaning a subset of the entries. For example:

_security-policy.properties_:
```
# Security policy marker file.
#
# All the files starting with the "security-module" prefix will be loaded and
# included as part of the global authorization policy.
#
```
_security-module-homes.properties_:
```
role.admin.home=Home
role.user.home=Dashboard
```
_security-module-priorities.properties_:
```
role.admin.priority=1
role.user.priority=0
```
_security-module-perspectives.properties_:
```
role.admin.permission.perspective.read=true
role.admin.permission.perspective.read.Dashboard=false
role.user.permission.perspective.read=false
role.user.permission.perspective.read.Home=true
role.user.permission.perspective.read.Dashboard=true
```

Notice, despite using the split approach, the _security-policy.properties_ must always be present as it is used as a marker file by the security subsystem in order to locate the other policy files.

This split mechanism allows for a better organization of the whole security policy. Use it at your best convenience.

