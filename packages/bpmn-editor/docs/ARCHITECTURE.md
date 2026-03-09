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

# @kie-tools/bpmn-editor :: ARCHITECTURE

- `<BpmnEditor>` is a regular Controlled Component, mainly controlled by the `model` property.
- The auto-generated `BPMN20__tDefinitions` type is used as the only representation of the BPMN workflow.
- Zustand and Immer are used to manage state, wrapped inside the `useBpmnEditorStoreApi()` hook. Calling `bpmnEditorStoreApi.setState(...)` will let you update any state, including the BPMN workflow.
- Complicated operations done to the BPMN workflow are managed by mutations inside the `mutations` directory.
- The BPMN diagram is rendered by `@kie-tools/xyflow-react-kie-diagram`, which uses ReactFlow.
