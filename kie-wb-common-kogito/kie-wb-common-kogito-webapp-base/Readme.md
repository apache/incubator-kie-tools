Kogito Webapp-base
==================

This module is meant as base to be *used* (declared as dependency) by Kogito' running and testing webapps and/or extended by
editor-specific common jar.


AuthoringPerspective
--------------------

This is the `WorkbenchPerspective` used by the Kogito' showcases.
Depending on the usage, it has to have one single content (*runtime/vscode* environments) or multiple contents (e.g. to host custom menus, *testing* environment). 
This decision depends on the *client* code, so  the `PerspectiveConfiguration` class has been implemented to provide a way to override default configuration.
User modules (showcases) have to create a `javax.enterprise.inject.Alternative` class *extending* `PerspectiveConfiguration`.
As examples, *runtime* showcases should use `StaticWorkbenchPanelPresenter` as *panel type* (this is the default) while *testing* showcases could use `MultiListWorkbenchPanelPresenter`.


TestingVFSService
-----------------

Wrapper around `VFSService` to provide filesystem functionalities (file/directory creation/load etc.)

