# Fluent Check API

The _AuthorizationManager_ methods introduced in the previous section are complemented with a fluent API that allows for checking permission in two different ways. For example:

```
boolean salesReportAvailable = authorizationManager.authorize(
                                    "myapp.reports.sales", user);
generateButton.setVisible(salesReportAvailable);
```
Using the fluent API could be expressed as:
```
authorizationManager.check("myapp.reports.sales", user)
    .granted(() -> generateButton.setVisible(true))
    .denied(() -> generateButton.setVisible(false));
```
or also as:
```
generateButton.setVisible(false);
authorizationManager.check("myapp.reports.sales", user)
    .granted(() -> generateButton.setVisible(true));
```

Feel free to use the more convenient approach.

The fluent API also works on _Resource_ instances:

```
Perspective myPerspective;

authorizationManager.check(myPerspective, user)
    .action(ResourceAction.READ)  // Optional, READ is the default
    .granted(() -> generateButton.setVisible(true))
    .denied(() -> generateButton.setVisible(false));
```




