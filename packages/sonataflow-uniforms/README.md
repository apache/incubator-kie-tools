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

## @kie-tools/sonataflow-uniforms

This package extends [`@kie-tools/uniforms-patternfly`](../uniforms-patternfly/README.md) with SonataFlow-specific [Uniforms](https://uniforms.tools/) components.

### Available Components

Each component is automatically registered as a Uniforms field, if used with a JSON schema.

- **`CodeEditorTextField`**: A code editor based on [@patternfly/react-code-editor](https://www.patternfly.org/components/code-editor) that supports syntax-highlighted editing of code (e.g., JSON, YAML) with validation.

### Basic usage

Follow uniforms-patternfly's [guide](../uniforms-patternfly/README.md#basic-usage) and in addition follow these steps.

#### 1. Install the required packages

Refer to the [step 1](../uniforms-patternfly/README.md#1-install-the-required-packages)

```sh
npm install @kie-tools/sonataflow-uniforms
```

#### 2. Define a schema

You can use the `CodeEditorTextField` by simply defining a field as `type: object`, without nested properties, in your JSON Schema:

```ts
const schema = {
  type: "object",
  properties: {
    customData: {
      type: "object",
      title: "Custom JSON",
      description: "Write your JSON using the editor",
    },
  },
};
```

#### 3. Create the bridge

Refer to the [step 3](../uniforms-patternfly/README.md#3-then-create-the-bridge).

#### 4. Use the form

In addition to [step 4](../uniforms-patternfly/README.md#4-finally-use-it-in-a-form-) we need to render our form this way:

```tsx
import * as React from "react";
import { useState } from "react";
import { Button } from "@patternfly/react-core";
import { AutoForm, ErrorsField } from "@kie-tools/uniforms-patternfly";
import { formSwfAutoFieldValue, SwfAutoFieldProvider } from "@kie-tools/sonataflow-uniforms";

import schema from "./schema";

export default function MyForm() {
  const [formApiRef, setFormApiRef] = useState<HTMLFormElement>();

  const submitFormData = (): void => {
    console.log("Submit!");
    formApiRef!.submit();
  };

  return (
    <>
      <AutoForm schema={bridge} ref={(ref: HTMLFormElement) => setFormApiRef(ref)}>
        <ErrorsField />
        <SwfAutoFieldProvider value={formSwfAutoFieldValue} />
      </AutoForm>
      <Button type="submit" onClick={submitFormData}>
        Submit
      </Button>
    </>
  );
}
```

### Contributing a New Component

To contribute a new custom component:

1.  Create a new component that accepts standard Uniforms props (`helperText, label, language, onChange, prefix, value`).
2.  Wrap it using `wrapField()` from `@kie-tools/uniforms-patternfly`.
3.  Export it using `connectField()` from `uniforms`.
4.  Update the `AutoFields` logic registered at `src/FormSwfAutoFieldValue.ts`.
5.  Add it to the `/src` directory and create a test in `/tests`.
6.  Document the component in the list above.

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
