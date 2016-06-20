# Extensibility

The security subsystem has been designed with extensibility in mind. That means it is possible to bring new resource types, new permissions and new security checks. To demonstrate so, let's take the Uberfire Tasks project introduced in the [UF Tasks](UF Tasks) section as real world example of integration with the security subsystem.

#### Domain model changes

First thing, it is to evaluate what resources requires protected access. For some of them there might exits a class representing such concept. This is indeed the case of the UTTasks' _Project_ class. Since projects can be read, created, edited, let's see how to secure those 3 actions.

Make the _Project_ class to implement the _Resource_ interface:

```
...
public class Project implements Resource {
...

    @Override
    public String getIdentifier() {
        return name;
    }
    @Override
    public ResourceType getResourceType() {
        return UTTasksResourceType.PROJECT;
    }
    @Override
    public List<Resource> getDependencies() {
        return null;
    }
...
```
* The resource unique identifier is the project's name
* A project has no other resource dependencies in terms of security
* The type of the resource is a custom one (see next)

```
package org.uberfire.shared.authz;

import org.uberfire.security.ResourceType;

/**
 * An extension of the {@link ResourceType} interface holding an enumeration with the
 * different UF Tasks related resource types subject to authorization management control.
 */
public enum UTTasksResourceType implements ResourceType {

    PROJECT;

    @Override
    public String getName() {
        return this.toString().toLowerCase();
    }
}
```

We also need to define the secured actions that can be applied to projects:

```
package org.uberfire.shared.authz;

import org.uberfire.security.ResourceAction;

public interface ProjectAction extends ResourceAction {

    ProjectAction CREATE = () -> "create";
    ProjectAction EDIT = () -> "edit";
}
```

Notice, the READ action is inherited from _ResourceAtion_.


The above changes are enough to start defining permissions on top of projects as well as runnning security checks to protect specific features.

#### Security checks

Let's consider the following security policy:

```
role.admin.permission.project.create=true
role.user.permission.project.create=false
```

In order to secure the project creation feature a security check is required in  the _ProjectsPresenter#newProject_  method:

```
import static org.uberfire.shared.authz.UTTasksResourceType.*;
import static org.uberfire.shared.authz.ProjectAction.*;
...
    @Inject
    AuthorizationManager authzManager;

    @Inject
    SessionInfo sessionInfo;

    public void newProject() {
        if (authzManager.authorize(PROJECT, CREATE, sessionInfo.getIdentity())) {
            newProjectPresenter.show( this );
        } else {
            Window.alert("Project creation NOT allowed!");
        }
    }
```
The fluent API is another valid alternative:

```
    public void newProject() {
        authzManager.check(PROJECT, sessionInfo.getIdentity()).action(CREATE)
            .granted(() -> newProjectPresenter.show( this ))
            .denied(() -> Window.alert("Project creation NOT allowed!"));
    }
```
The @ResourceCheck annotation is also a valid one:

```
import static org.uberfire.shared.authz.ProjectConstants.*;
...
    @ResourceCheck(type=PROJECT, action=CREATE, onDenied="onCreateDenied")
    public void newProject() {
        newProjectPresenter.show( this );
    }

    public void onCreateDenied() {
        Window.alert("Project creation NOT allowed!");
    }
```
The annotation approach it requires to define a _ProjectContants_ class contaning the string definitions for the project type and the project actions.

```
package org.uberfire.shared.authz;

public class ProjectConstants {

    public static final String PROJECT = "project";
    public static final String READ = "read";
    public static final String CREATE = "create";
    public static final String EDIT = "edit";
}
```

#### Custom permissions

Sometimes it is needed to check a concrete feature not tied to any specific resource. For example, the main menu in the UF Tasks project contains an option named "About" which must   be only visible to the _admin_ role.

The code below adds the _About_ option to the main menu (see _ShowcaseEntryPoint.java_):

```
        .newTopLevelMenu( "About" )
        .identifier( "general.about" )
        .respondsWith(() -> Window.alert( "UF tasks showcase" ))
        .endMenu()
```

The menu item identifier can be used to register a permission into the security policy and make it only available to users with _admin_ role:

```
role.admin.permission.uftasks.about=true
role.user.permission.uftasks.about=false
```

The security checks available in the _AuthorzationManager_  can be applied on the new permission as well:

```
if (authzManager.auhtorize("uftasks.about", sessionInfo.getIdentity())) {
    ...
}
```
