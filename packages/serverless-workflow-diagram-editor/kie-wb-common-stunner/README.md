# Stunner Modelling Tool

Stunner is a multi-purpose graph based modelling tool for the web.

It is being implemented on top of [**GWT**](https://www.gwtproject.org/) / [**JsInterop**](https://www.gwtproject.org/doc/latest/DevGuideCodingBasicsJsInterop.html), and relies on HTML Canvas for rendering the views ([**Lienzo**](../lienzo-core/README.md)).

## Getting started

Get started by using the [**Serverless Workflow Diagram Editor**](../sw-editor/README.md).

## Development prerequisites for LINUX Users

Change the _inotify_ limits configuration as noted below to avoid further issues within the IDE.

1.  Close the IDE
2.  Edit `/etc/sysctl.conf` and add the following content:

    fs.inotify.max_user_watches = 524288
    fs.inotify.max_user_instances = 524288

3.  Edit `/etc/security/limits.conf` and add the following content:

    <user> soft nofile 4096
    <user> hard nofile 10240

## Development prerequisites for Windows Users

1. Enable long paths in Windows

- open regedit -> `HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\Control\FileSystem` -> Change `LongPathsEnabled` from `0` to `1`

2. Enable long paths in Git

- Open GitBash -and type `git config --system core.longpaths true`

3. IDEA executable

- The default setup creates a shortcut to idea.exe. Be sure to change to idea64.exe, otherwise you'll be running 32-bits version

## Building Stunner in IntelliJ

In order to build `kie-wb-common-stunner-lienzo`, internal module in IntelliJ, it is needed to add a `Maven Goal` to the `Run` configurations of the IDE.

1. Open the menu `Run`
2. Click on `Edit Configurations...`
3. In the session `Before Launch` click on `+` and select `Run Maven Goal`
4. Select as `Working directory` the path to module `kie-wb-common-stuner-lienzo` and for `Command line` the phase `generate-resources`

When `Maven Goal` is executed the required resources will be added to the build.
