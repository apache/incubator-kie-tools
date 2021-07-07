# How to create a Kogito editor React component

All Kogito editor React components live in the `packages` directory and follow the convention of **components** and **loaders**.

- **Components** - all elements an editor may be a marshaller, a dialog, a Boxed Expression editor, or even a FEEL editor.

- **Loaders** - this is more specific. It teaches how each editor (BPMN, DMN, or Scenario Simulation) may load their components.

In this guide, we will create a **component** and add it to GWT in 5 steps.

### 1. Create a new component

Let's use the following script to create the minimal boilerplate code that a React component needs:

```bash
yarn new-component
```

Once you execute this, insert the name of your new component. In my case, In my case, I will call it `feel-dialog`.

### 2. Run the development web app

As I named my component as `feel-dialog`, I type this command to access it:

```bash
cd packages/feel-dialog-component
```

> **Notice**: The directory already has the proper suffix by convention!

Now you may run this to install the default dependencies of your component and development web app:

```bash
yarn install

cd showcase
yarn install
```

Finally, you're ready to run the web app and see your component alive:

```
yarn start
```

### 3. Expose with the a Loader

Now, we need to get this component in some loader. So, your GWT editor (BPMN, DMN, or Scenario Simulation) will be able to render it.

In this example, I will use the `dmn-loader`. Thus, we may:

- Open the `packages/dmn-loader` directory
- Add the `feel-dialog-component` in the `package.json` as a dependency
- And, finally, add the following snippet in the `src/index.tsx` file:

```typescript
import { ItWorks } from "feel-dialog-component";

const renderFeelDialog = (selector: string) => {
  ReactDOM.render(<ItWorks />, document.querySelector(selector));
};

export { renderFeelDialog };
```

Now the loader knows how to render the `FeelDialog`.

### 4. Consume from the GWT editors

In the GWT world, we consume the React component by adding the `renderFeelDialog` interface in the **JsInterop-loader** class:

```java
package org.kie.workbench.common.dmn.client.js;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;

@JsType(isNative = true)
public class DMNLoader {

    @JsMethod(namespace = "__KIE__DMN_LOADER__")
    public static native void renderFeelDialog(final String selector);
}
```

> **Notice**: For BPMN components, please use the `"__KIE__BPMN_LOADER__"` namespace.

### 5. Let's render it!

Finally, now you can render your React component anywhere in the GWT world, like this:

```java
final Element container = DomGlobal.document.createElement("div");

container.id = "container";
DomGlobal.document.body.appendChild(container);

DMNLoader.renderFeelDialog("#container");
```

Ta-dah! Now, if you'd like to continue evolving your new component in a rich development environment, check [this other guide](./wire.md).

## Contributing

If you find anything unclear in this guide, please contribute. But, before you start, read the [contribution guide](../../CONTRIBUTING.md).
