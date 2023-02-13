# Basic usage

`uniforms` is a plugin for React to be able to create dynamic forms with built-in state management and form validation.
`uniforms` provides you with simple re-usable form components which allows for rapid prototyping and cleaner React components.

This package extends uniforms to provide [Patternfly React](https://www.patternfly.org/v4/) components inside your forms.
For more information about `uniforms` please go to https://uniforms.tools/

Looking for building mobile enabled forms? Check [Uniforms-ionic](https://github.com/aerogear/uniforms-ionic) package that provides Ionic extensions

### 1. Install the required packages

To start using uniforms, we have to install three independent packages:

1. Core
2. Bridge
3. Theme

In this example, we will use the JSON Schema to describe our desired data format and style our form using the Pattenfly UI theme.

```shell
npm install uniforms@^3.10.2
npm install uniforms-bridge-json-schema@^3.10.2
npm install @kie-tools/uniforms-patternfly
npm install @patternfly/react-core @patternfly/react-icons
```

Don't forget that it's necessary to correctly load the styles from Patternfly. To do it, we recommend taking a look into the
[Patternfly React Seed](https://github.com/patternfly/patternfly-react-seed), or you can simply load the styles directly into
your `index.html` like in the example app of this repo.

Obs: If you use a previous version of the `tslib` indirectly (version 1), it should be necessary to add this dependency as well.

```shell
npm install tslib@^2.3.1
```

### 2. Start by defining a schema

After we've installed required packages, it's time to define our schema. We can do it in a plain JSON, which is a valid JSON Schema instance:

```js
const schema = {
  type: "object",
  properties: {
    foo: {
      type: "string",
    },
  },
};
```

### 3. Then create the bridge

Now that we have the schema, we can create the uniforms bridge of it, by using the corresponding uniforms bridge package.
Creating the bridge instance is necessary - without it, uniforms would not be able to process form generation and validation.
As we are using the JSON Schema, we have to import the `uniforms-bridge-json-schema` package. Also, because we're doing an
example of a JSON Schema, it's necessary to use a JSON Schema validation library, and in this example we'll be using the AJV.

```js
import { JSONSchemaBridge } from "uniforms-bridge-json-schema";
import AJV from "ajv";

const ajv = new Ajv({ allErrors: true, useDefaults: true });

function createValidator(schema) {
  const validator = ajv.compile(schema);

  return (model) => {
    validator(model);
    return validator.errors?.length ? { details: validator.errors } : null;
  };
}

const bridge = new JSONSchemaBridge(schema, createValidator(schema));
```

### 4. Finally, use it in a form! 🎉

Uniforms theme packages provide the `AutoForm` component, which is able to generate the form based on the given schema.
All we have to do now is to pass the previously created Bridge to the `AutoForm`:

```js
import * as React from "react";
import { AutoForm } from "@kie-tools/uniforms-patternfly/dist/esm";

import schema from "./schema";

export default function MyForm() {
  return <AutoForm schema={bridge} onSubmit={console.log} />;
}
```

And that's it! `AutoForm` will generate a complete form with labeled fields, errors list (if any) and a submit button.

Also, it will take care of validation and handle model changes. In case you need more advanced feature, take a deeper look
into the Uniforms docs.
