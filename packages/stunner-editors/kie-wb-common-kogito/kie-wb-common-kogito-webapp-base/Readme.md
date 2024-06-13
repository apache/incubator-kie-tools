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
