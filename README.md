# Basic usage

`uniforms` is a plugin for React to be able to create dynamic forms with built-in state management and form validation. `uniforms` provides you with simple re-usable form components which allows for rapid prototyping and cleaner React components.

### 1. Install the required packages

To start using uniforms, we have to install three independent packages:

1. Core
2. Bridge
3. Theme

In this example, we will use the JSONSchema to describe our desired data format and style our form using Semantic UI theme.

```shell
npm install uniforms@3.0.0-alpha.5 // or latest alpha
npm install uniforms-bridge-simple-schema2@3.0.0-alpha.5 // or latest alpha
npm install simpl-schema
npm install uniforms-patternfly
```

<!-- **Note**: When using a themed package, remember to include correct styles! If you are willing to run this example by yourself,
have a read on [Semantic UI React's theme usage](https://react.semantic-ui.com/usage/#theme). -->

### 2. Start by defining a schema

After we've installed required packages, it's time to define our Guest schema. We can do it in a plain JSON, which is a valid JSONSchema instance:

```javascript
import SimpleSchema from 'simpl-schema';

const schema = new SimpleSchema({
  name: {
    type: String
  },
  lastname: {
    type: String
  },
  date: {
    type: Date
  }
});
```

### 3. Then create the bridge

Now that we have the schema, we can create the uniforms bridge of it, by using the corresponding uniforms schema-to-bridge package.
Creating the bridge instance is necessary - without it, uniforms would not be able to process form generation and validation.
As we are using the SimplSchema, we have to import the `uniforms-bridge-simple-schema2` package.

```js
import { SimpleSchema2Bridge } from 'uniforms-bridge-simple-schema-2';

...

export default new SimpleSchema2Bridge(schema);
```

Just to recap, the whole `schema.js` file looks like this:

```js
import SimpleSchema from 'simpl-schema';
import { SimpleSchema2Bridge } from 'uniforms-bridge-simple-schema-2';

const schema = new SimpleSchema({
  name: {
    type: String
  },
  lastname: {
    type: String
  },
  date: {
    type: Date
  }
});

export default new SimpleSchema2Bridge(schema);
```

### 4. Finally, use it in a form!

Uniforms theme packages provide the `AutoForm` component, which is able to generate the form based on the given schema.
All we have to do now is to pass the previously created GuestSchema to the `AutoForm`:

```js
import React from 'react';
import { AutoForm } from uniforms-semantic;

import schema from './schema';

export default function GuestForm() {
  return <AutoForm schema={schema} onSubmit={console.log} />;
}
```

And that's it! `AutoForm` will generate a complete form with labeled fields, errors list (if any) and a submit button.

Also, it will take care of validation and handle model changes.