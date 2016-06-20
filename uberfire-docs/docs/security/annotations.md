# Security Annotations

Two security annotations are provided as an alternative to the security checks methods provided by the _AuthorizationManager_ interface.

* **@ResourceCheck**

```
import static org.uberfire.shared.authz.ProjectConstants.*;
...

@ResourceCheck(type=PROJECT, action=CREATE)
public void newProject() {
    newProjectPresenter.show( this );
}
```

Every time the method _newProject_ is called the security check defined in the annotation is processed, causing the method body to be executed only if the permission is granted.


* **@PermsissionCheck**

Annotation checks are supported for plain permissions as well.

```
@PermissionCheck("myapp.reports.sales")
public void showSalesReport() {
    salesReportsPresenter.show();
}
```

#### Callbacks

Both annotations provide callback support:

```
@ResourceCheck(type=PROJECT, action=CREATE,
                onGranted="onCreateGranted",
                onDenied="onCreateDenied")
public void newProject() {
    newProjectPresenter.show();
}

public void onCreateGranted() {
    GWT.log("Project creation allowed");
}

public void onCreateDenied() {
    GWT.log("Project creation NOT allowed");
}
```

Callbacks can be used to add extra processing logic.

The example above without annotations:

```
@Inject
AuthorizationManager authzManager;
...

public void newProject() {
    if (authzManager.authorize(PROJECT, CREATE)) {
        newProjectPresenter.show();
        onCreateGranted();
    }
    else {
        onCreateDenied();
    }
}
...
```

#### Restrictions

The security annotations are can only be used under certain conditions:

* Only work on client side code
* Only work on CDI managed beans
* Only can be added to methods that return "void"
* Only work if the method is called from an external component.
* For inner component calls the solution is to use a delegate or a "self" instance.


