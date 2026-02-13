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

# @kie-tools/dmn-uniforms-patternfly-form-wrapper

This package provides React components for rendering DMN (Decision Model and Notation) input forms and output results using Uniforms with PatternFly styling.

## Features

- **DMN Input Forms**: Automatically generate forms from DMN JSON schemas for decision inputs
- **DMN Output Display**: Render decision evaluation results with status indicators
- **PatternFly Integration**: Styled with PatternFly components for consistent UI/UX
- **Validation**: Built-in validation for DMN-specific data types and constraints
- **Internationalization**: Multi-language support (English and German)
- **Type Safety**: Full TypeScript support

## Installation

```bash
pnpm add @kie-tools/dmn-uniforms-patternfly-form-wrapper
```

## Components

### FormDmn

A form component for rendering DMN decision inputs based on JSON Schema.

```tsx
import { FormDmn } from "@kie-tools/dmn-uniforms-patternfly-form-wrapper";

<FormDmn formSchema={jsonSchema} model={inputData} onChange={(model) => console.log(model)} locale="en" />;
```

**Props:**

- `formSchema`: JSONSchema4 - The JSON schema defining the form structure
- `model`: InputRow - The current form data
- `onChange`: (model: InputRow) => void - Callback when form data changes
- `locale`: string (optional) - Language locale (defaults to browser language)

### FormDmnOutputs

A component for displaying DMN decision evaluation results.

```tsx
import { FormDmnOutputs } from "@kie-tools/dmn-uniforms-patternfly-form-wrapper";

<FormDmnOutputs
  results={decisionResults}
  differences={resultDifferences}
  locale="en"
  notificationsPanel={true}
  openEvaluationTab={() => {}}
  openBoxedExpressionEditor={(nodeId) => {}}
  openedBoxedExpressionEditorNodeId={undefined}
/>;
```

**Props:**

- `results`: DecisionResult[] (optional) - Array of decision evaluation results
- `differences`: Array<DeepPartial<DecisionResult>> (optional) - Changes between evaluations
- `locale`: string (optional) - Language locale
- `notificationsPanel`: boolean - Whether notifications panel is available
- `openEvaluationTab`: () => void (optional) - Callback to open evaluation details
- `openBoxedExpressionEditor`: (nodeId: string) => void (optional) - Callback to open expression editor
- `openedBoxedExpressionEditorNodeId`: string | undefined - Currently opened expression editor node

## Validation

The `FormDmnValidator` class extends the base validator with DMN-specific validation rules:

- Format validation for duration types
- Validation for DMN allowed values (`x-dmn-allowed-values`)
- Validation for DMN type constraints (`x-dmn-type-constraints`)
- Custom error messages for DMN-specific validation failures

## Result Display Features

The `FormDmnOutputs` component provides:

- **Status Indicators**: Visual feedback for evaluation status (succeeded, skipped, failed)
- **Nested Data Rendering**: Recursive rendering of complex objects and arrays
- **Date Formatting**: UTC dates with timezone tooltips
- **Highlight Changes**: Visual indication of updated results
- **Expression Editor Integration**: Quick access to boxed expression editor
- **Error Handling**: Graceful error display with error boundaries

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
