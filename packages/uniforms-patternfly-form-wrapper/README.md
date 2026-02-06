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

# @kie-tools/uniforms-patternfly-form-wrapper

A React wrapper library for building dynamic forms from JSON Schema using Uniforms with PatternFly styling. This package provides a foundation for creating form-based applications with automatic form generation, validation, and error handling.

## Features

- **JSON Schema to Form**: Automatically generate forms from JSON Schema definitions
- **PatternFly Styling**: Built-in PatternFly components for consistent UI/UX
- **Validation**: Comprehensive validation using AJV (Another JSON Schema Validator)
- **Error Handling**: Built-in error boundaries and status management
- **Type Safety**: Full TypeScript support
- **Internationalization**: Multi-language support (English and German)
- **Auto-save**: Optional auto-save functionality with configurable delay
- **Customizable**: Extensible validator and bridge classes

## Installation

```bash
pnpm add @kie-tools/uniforms-patternfly-form-wrapper
```

## Core Components

### FormComponent

The main component for rendering forms with full state management and validation.

```tsx
import { FormComponent } from "@kie-tools/uniforms-patternfly-form-wrapper";

<FormComponent
  formSchema={jsonSchema}
  formInputs={formData}
  setFormInputs={setFormData}
  formError={hasError}
  setFormError={setHasError}
  locale="en"
  notificationsPanel={true}
  validator={customValidator}
  onSubmit={(model) => console.log(model)}
  onValidate={(model, error) => console.log(model, error)}
/>;
```

**Props:**

- `formSchema`: Schema - JSON Schema defining the form structure
- `formInputs`: Input - Current form data
- `setFormInputs`: React.Dispatch - State setter for form data
- `formError`: boolean - Whether the form has errors
- `setFormError`: React.Dispatch - State setter for error state
- `locale`: string - Language locale (defaults to browser language)
- `notificationsPanel`: boolean - Whether notifications panel is available
- `validator`: Validator (optional) - Custom validator instance
- `onSubmit`: (model: object) => void (optional) - Submit callback
- `onValidate`: (model: object, error: object) => void (optional) - Validation callback
- `openValidationTab`: () => void (optional) - Callback to open validation details
- `autoSave`: boolean (optional) - Enable auto-save
- `autoSaveDelay`: number (optional) - Auto-save delay in milliseconds
- `showInlineError`: boolean (optional) - Show inline validation errors
- `placeholder`: boolean (optional) - Show placeholders
- `entryPath`: string (optional) - Path to form entry in schema (default: "definitions")
- `propertiesEntryPath`: string (optional) - Path to properties in schema (default: "definitions")
- `removeRequired`: boolean (optional) - Remove required field constraints

### FormBase

Lower-level component for rendering forms with pre-configured state.

```tsx
import { FormBase } from "@kie-tools/uniforms-patternfly-form-wrapper";

<FormBase
  i18n={i18n}
  formStatus={FormStatus.WITHOUT_ERROR}
  jsonSchemaBridge={bridge}
  formModel={formData}
  setFormError={setError}
  setFormRef={setRef}
  errorBoundaryRef={errorBoundaryRef}
  notificationsPanel={true}
  onSubmit={(model) => console.log(model)}
  onValidate={(model, error) => console.log(model, error)}
/>;
```

## Hooks

### useForm

A custom hook that manages form state, validation, and lifecycle.

```tsx
import { useForm } from "@kie-tools/uniforms-patternfly-form-wrapper";

const { onSubmit, onValidate, formStatus, jsonSchemaBridge, errorBoundaryRef, setFormRef } = useForm({
  formError,
  setFormError,
  formInputs,
  setFormInputs,
  formSchema,
  validator,
  i18n,
  entryPath: "definitions",
  propertiesEntryPath: "definitions",
  removeRequired: false,
});
```

**Returns:**

- `onSubmit`: Form submit handler
- `onValidate`: Form validation handler
- `formStatus`: Current form status (FormStatus enum)
- `jsonSchemaBridge`: Bridge instance for Uniforms
- `errorBoundaryRef`: Reference to error boundary
- `setFormRef`: Form element reference setter

## Form Status

The package provides different form states:

```typescript
enum FormStatus {
  WITHOUT_ERROR, // Form is valid and ready
  VALIDATOR_ERROR, // Schema validation failed
  AUTO_GENERATION_ERROR, // Form generation failed
  EMPTY, // No form inputs defined
}
```

Each status has a corresponding status component:

- `EmptyFormStatus` - Displayed when no inputs are defined
- `AutoGenerationErrorFormStatus` - Displayed when form generation fails
- `ValidatorErrorFormStatus` - Displayed when schema validation fails

## Validation

### Validator Class

The base validator class uses AJV for JSON Schema validation.

```typescript
import { Validator } from "@kie-tools/uniforms-patternfly-form-wrapper";

const validator = new Validator(i18n);

// Create a validator function
const validatorFn = validator.createValidator(jsonSchema);

// Get a bridge for Uniforms
const bridge = validator.getBridge(jsonSchema);
```

**Methods:**

- `createValidator(formSchema)`: Creates a validation function for the schema
- `getBridge(formSchema)`: Creates a FormJsonSchemaBridge instance

### Custom Validators

Extend the `Validator` class to create custom validators:

```typescript
import { Validator } from "@kie-tools/uniforms-patternfly-form-wrapper";

class CustomValidator extends Validator {
  constructor(i18n) {
    super(i18n);
    // Add custom formats or keywords
  }

  public createValidator(formSchema) {
    // Custom validation logic
    return super.createValidator(formSchema);
  }
}
```

## JSON Schema Bridge

The `FormJsonSchemaBridge` class bridges JSON Schema with Uniforms.

```typescript
import { FormJsonSchemaBridge } from "@kie-tools/uniforms-patternfly-form-wrapper";

const bridge = new FormJsonSchemaBridge(jsonSchema, validatorFunction, i18n);
```

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
