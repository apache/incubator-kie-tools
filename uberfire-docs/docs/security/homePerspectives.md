# Home Perspectives

Every time a user logs in to an Uberfire application the system need to resolve what is the default perspective to show up. Next are the steps applied by this resolution procedure:

1.- The authorization policy is asked about the home perspective setting assigned to the user. For example:

```
role.admin.home=AdminDashboard
role.manager.home=ManagerDashboard
```
For users with _admin_ role the perspective with _id=AdminDashboard_ is taken as the default.

If no home perspective is defined or access is not granted then the step #2 (see below) is evaluated.

NOTE: If the user belongs to more than one user role/group and there is more than one home
perspective set then the role/group with the highest priority wins. If priorities are
still the same then the first one is taken. Further details about priorities and conflict
resolution are introduced in the [Permission Resolution](Permission Resolution) section.


2.- If no home perspective is set then the perspective marked as default is taken:

```
@WorkbenchPerspective(identifier = "HomePerspective", isDefault = true)
public class HomePerspective extends Composite {
...
```


Notice, a read permission over the target perspective is always required, which means making sure that the read permission is not explicitily denied. This applies to both steps 1 & 2.

In case it's not possible to determine what is the home perspective to redirect the application will show the following alert `No home perspective available!`



