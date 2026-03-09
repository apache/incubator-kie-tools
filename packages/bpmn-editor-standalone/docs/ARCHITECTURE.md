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

# @kie-tools/bpmn-editor-standalone :: ARCHITECTURE

- Webpack is used to bundle minified code which includes all CSS and assets necessary for the BPMN Editor to render.
- To avoid global object pollution and conflicts with the application's CSS, the BPMN Editor will be loaded inside an `iframe`.
- [**_Multiplying Architecture_** ](../../../repo/MULTIPLYING_ARCHITECTURE.md) is used to wrap the BPMN Editor inside an **`Envelope`** and handle communication with the `iframe`.
