<!--
   Licensed to the Apache Software Foundation (ASF) under one
   or more contributor license agreements.  See the NOTICE file
   distributed with this work for additional information
   regarding copyright ownership.  The ASF licenses this file
   to you under the Apache License, Version 2.0 (the
   "License"); you may not use this file except in compliance
   with the License.  You may obtain a copy of the License at
     http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing,
   software distributed under the License is distributed on an
   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
   KIND, either express or implied.  See the License for the
   specific language governing permissions and limitations
   under the License.
-->

# Stunner Modelling Tool

Stunner is a multi-purpose graph based modelling tool for the web.

It is being implemented on top of [**GWT**](https://www.gwtproject.org/) / [**JsInterop**](https://www.gwtproject.org/doc/latest/DevGuideCodingBasicsJsInterop.html), and relies on HTML Canvas for rendering the views ([**Lienzo**](../lienzo-core/README.md)).

## Getting started

Get started by using the [**BPMN Editor's webapp**](./kie-wb-common-stunner-sets/kie-wb-common-stunner-bpmn/kie-wb-common-stunner-bpmn-kogito-runtime/README.md).

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
