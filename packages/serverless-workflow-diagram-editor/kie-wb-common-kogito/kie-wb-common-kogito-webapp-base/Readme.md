# Kogito Webapp-base

This module is meant as base to be _used_ (declared as dependency) by Kogito' running and testing webapps and/or extended by
editor-specific common jar.

## AuthoringPerspective

This is the `WorkbenchPerspective` used by the Kogito' showcases.
Depending on the usage, it has to have one single content (_runtime/vscode_ environments) or multiple contents (e.g. to host custom menus, _testing_ environment).
This decision depends on the _client_ code, so the `PerspectiveConfiguration` class has been implemented to provide a way to override default configuration.
User modules (showcases) have to create a `javax.enterprise.inject.Alternative` class _extending_ `PerspectiveConfiguration`.
As examples, _runtime_ showcases should use `StaticWorkbenchPanelPresenter` as _panel type_ (this is the default) while _testing_ showcases could use `MultiListWorkbenchPanelPresenter`.

## TestingVFSService

Wrapper around `VFSService` to provide filesystem functionalities (file/directory creation/load etc.)
