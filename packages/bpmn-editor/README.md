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

## @kie-tools/bpmn-editor

The Apache KIE BPMN Editor.

This package exposes the `<BpmnEditor>` React component. Features include:

- Support for the BPMN 2.0 specification
- Backwards compatibility with the [Apache KIE BPMN Editor (classic)](https://www.npmjs.com/package/@kie-tools/kie-editors-standalone) — no longer available (last available in Apache KIE 10.1).
- Extension points compatible with the jBPM Engine and Kogito.
  - WorkItemDefinition (`.wid`) files for bindings with WorkItemHandlers (WIHs).
  - Acitivity SLAs
  - onEntry/onExit scripts
- Integration with the Drools Engine and Kogito via the Business Rule Task
  - DMN-based Decisions (incl. auto-fill)
  - DRL-based Rules
- Custom Tasks with custom Properties Panel components
- Correlations panel dedicated for event-driven workflows
- Exporting the BPMN diagram to SVG
- Built in the "[Controlled component](https://legacy.reactjs.org/docs/forms.html#controlled-components)" pattern
- Type-safe model representation (incl. extension points) powered by [@kie-tools/bpmn-marshaller](../bpmn-marshaller/README.md)

The Apache KIE BPMN Editor is built with [Reactflow](https://reactflow.dev/) and shares a lot of good qualities from the Apache KIE DMN Editor, like:

- Grid snapping
- Commands-based programmatic API (E.g., Binding to keyboard shortcuts)
- Infinite canvas
- Multi-node and edge selection

---

### Usage

- Add the `<BpmnEditor>` component to your React application; _or_
- Use the Apache KIE BPMN Editor as a standalone JavaScript/TypeScript library. See [@kie-tools/bpmn-editor-standalone](../bpmn-editor-standalone/README.md).

---

### Screenshots

> ![A screenshot of a Hiring process modeled with Apache KIE BPMN Editor](docs/images/readme/hiring.bpmn.png)
> A screenshot of a Hiring process created with Apache KIE BPMN Editor

---

## For development information see:

- 👉 [DEV.md](./docs/DEV.md)
- 👉 [TEST.md](./docs/TEST.md)
- 👉 [ARCHITECTURE.md](./docs/ARCHITECTURE.md)

---

Apache KIE (incubating) is an effort undergoing incubation at The Apache Software
Foundation (ASF), sponsored by the name of Apache Incubator. Incubation is
required of all newly accepted projects until a further review indicates that
the infrastructure, communications, and decision making process have stabilized
in a manner consistent with other successful ASF projects. While incubation
status is not necessarily a reflection of the completeness or stability of the
code, it does indicate that the project has yet to be fully endorsed by the ASF.

Some of the incubating project’s releases may not be fully compliant with ASF
policy. For example, releases may have incomplete or un-reviewed licensing
conditions. What follows is a list of known issues the project is currently
aware of (note that this list, by definition, is likely to be incomplete):

- Hibernate, an LGPL project, is being used. Hibernate is in the process of
  relicensing to ASL v2
- Some files, particularly test files, and those not supporting comments, may
  be missing the ASF Licensing Header

If you are planning to incorporate this work into your product/project, please
be aware that you will need to conduct a thorough licensing review to determine
the overall implications of including this work. For the current status of this
project through the Apache Incubator visit:
https://incubator.apache.org/projects/kie.html
