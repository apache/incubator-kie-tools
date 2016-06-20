# Workbench Assets

Uberfire implements access control mechanisms for most of its workbench assets. Read only permissions are supported for menu items, perspectives, screens, editors, splash screens and popup screens. That means, it is possible to add new permission entries into the authorization policy in case protected access is required for any of those asset types.

Imagine, for instance, we are developing a workbench screen annotated as  `@WorkbenchPerspective(identifier = "HomeForManagers")`.  By default, assets are not protected unless permissions are added to the security policy. So, imagine now, we want to grant access only to individual perspectives:

```
# Manager role only have access to the HomeForManagers perspective
role.manager.permission.perspective.read=false
role.manager.permission.perspective.read.HomeForManagers=true
```

The above is an example of a `deny all grant a few` strategy.

Next is an example of the opposite `grant all deny a few` strategy:

```
# Admin role users have access to all the perspectives but the HomeForManagers
role.admin.permission.perspective.read=true
role.admin.permission.perspective.read.HomeForManagers=false
```

As shown above the policy is flexible enough to model the most common scenarios. Besides, it can be used to protect other workbench assets. For instance:

```
# A superuser role can read everything
role.superuser.permission.perspective.read=true
role.superuser.permission.screen.read=true
role.superuser.permission.popup.read=true
role.superuser.permission.splash.read=true
role.superuser.permission.editor.read=true
```

#### Menu items

Menus can be used to implement nagivation features across the workbench. It is possible to define menu items linked to perspectives. For instance, consider the following menu structure which might be a perfect an example of the main menu of a typical Uberfire app:

```
import static org.uberfire.workbench.model.menu.MenuFactory.*;
...
    @Inject
    private WorkbenchMenuBar menubar;

    private void setupMenu(@Observes final ApplicationReadyEvent event) {
        final Menus menus =
        newTopLevelMenu("Tasks")
            .perspective("TasksPerspective")
            .endMenu()
        .newTopLevelMenu("Dashboard")
            .perspective("DashboardPerspective")
            .endMenu()
            .build();

        menubar.addMenus( menus );
    }
...
```

We have 2 menu entries visible in this app's top level menu, both linked to perspectives. The method `perspective(...)` it is used to set the target perspective identifier the system will go to after clicking on the menu item. Every menu item will show/hide depending on the perspective access permission granted to the user. In fact, the perspective menu items above could be defined also as:

```
        newTopLevelMenu("Tasks")
            .withPermission("perspective.read.TasksPerspective")
            .respondsWith(() -> placeManager.goTo("TasksPerspective"))
            .endMenu()
```

or even:

```
        newTopLevelMenu("Tasks")
            .withPermission(new ResourceRef("TasksPerspective", ActivityResourceType.PERSPECTIVE))
            .respondsWith(() -> placeManager.goTo("TasksPerspective"))
            .endMenu()
```

Despite the last two snippets are a more verbose way of defining a protected perspective menu item, they can be taken as examples of the `withPermission(...)` usage for linking menu items to any type of resource.

Menu items can also be linked with ad-hoc permissions, for example:

```
        .newTopLevelMenu("About")
            .withPermission("uftasks.about")
            .respondsWith(() -> Window.alert( "Tasks App 1.0.0" ))
            .endMenu()
```

The menu item will be available provided the right permission is inserted into the security policy:

```
role.manager.permission.uftasks.about=false
```
