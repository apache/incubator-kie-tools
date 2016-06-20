# Permission Checks

A permission is, in the end, just a name representing a feature, a resource's action or just "something" the developer wants to be under access control.

Actions over resources introduced in the previous section are just another mechanism of defining permissions. For instance:

```
boolean result = authzManager.authorize(perspective1, ResourceAction.READ, user);
```
It is equivalent to:
```
boolean result = authzManager.authorize("perspective.read.perspective1", user);
```
In this second example the low-level permission name is being used instead of the high level style based on resource actions. In the first one, what the _AuthorizatiopnManager_ is doing is just getting the underlying permission name and passing it to the method based on permission name.

It is always recommended to use the high level API, based on resources, since permission names are considered part of the internal resource implementation which is actually subject to change.

In some cases, though, the developer might prefer to rely on permission names, because a resource implementation could make no sense or just because it is considered the easier approach. For example, imagine a "sales report generation" feature the developer wants to limit its availability.

```
public static final String MYAPP_REPORTS_SALES = "myapp.reports.sales";
boolean result = authzManager.authorize(MYAPP_REPORTS_SALES, user);
```
Such permission can be added to the authorization policy:
```
# Employees do not see any reports
role.admin.permission.myapp.reports=false

# Managers have only access to some specific reports
role.admin.permission.myapp.reports=false
role.admin.permission.myapp.reports.sales=true
```
Notice the "dot based" approach to name permissions. Every dot added to the permission's name creates a new level in the permission hierarchy. In the example above the nodes defined are: `myapp`, `reports` and `sales`.

The permissions in a tree inherit from its parent node, so if the `myapp.reports` permission is granted then any child permission is also granted by default.

Following this very simple mechanism an entire permission tree can be created. Once the permissions tree nomenclature is defined, the `grant all deny a few` or the `deny all grant a few` strategies can be used to grant/deny access to any of its nodes.
