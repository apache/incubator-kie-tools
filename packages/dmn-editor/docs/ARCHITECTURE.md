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

# @kie-tools/dmn-editor :: ARCHITECTURE

- `<DmnEditor>` is a regular Controlled Component, mainly controlled by the `model` property.
- The auto-generated `DMN_LATEST__tDefinitions` type is used as the only representation of the DMN decision.
- Zustand and Immer are used to manage state, wrapped inside the `useDmnEditorStoreApi()` hook. Calling `dmnEditorStoreApi.setState(...)` will let you update any state, including the DMN decision.
- Complicated operations done to the DMN decision are managed by mutations inside the `mutations` directory.
- The DMN diagram is currently not rendered by [`@kie-tools/xyflow-react-kie-diagram`](../../xyflow-react-kie-diagram/), which uses ReactFlow, but rather uses its own diagram rendering internal framework that gave origin to [`@kie-tools/xyflow-react-kie-diagram`](../../xyflow-react-kie-diagram/).
