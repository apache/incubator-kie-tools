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

# 'To do' list View

You can read [here](https://blog.kie.org/2020/10/kogito-tooling-examples-how-to-create-a-custom-view.html) a step-by-step tutorial of how this custom View was built.

This package exposes the necessary files for you to create a 'To do' list Envelope.

It's divided in the following submodules:

1. `api`
   - Provides the APIs that the Channel/Envelope expose to each other.
1. `embedded`
   - Provides a convenience React component to embed a 'To do' list View in a Web application.
1. `envelope`
   - Provides the necessary class for a Channel to create a 'To do' list Envelope.
1. `vscode`
   - Provides a convenience class to create a Webview inside a VS Code Extension.
