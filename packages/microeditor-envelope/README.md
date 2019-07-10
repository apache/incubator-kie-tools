Core
==

This package provides the public AppFormer.js API alongside with its Marshalling capabilities.

Usage
--

Imagine you have a ReactComponent

```typescript jsx
import * as React from "react";

class MyReactComponent extends React.Component<{ exposing: (self: MyReactComponent) => void }, {}> {
  public fetchDataAndUpdate() {
    //fetches some data and updates state
  }

  public render() {
    return <div>...</div>;
  }
}
```

Turning it into an `AppFormer.Screen` or `AppFormer.Perspective` is simple:

```typescript jsx
import * as React from "react";
import * as AppFormer from "appformer-js";

export class MyScreen extends AppFormer.Screen {
  private screen: MyReactComponent;

  constructor() {
    super("my-screen");
    this.af_isReact = true;
    this.af_componentTitle = "MyScreen title";
  }

  af_onOpen(): void {
    this.screen.fetchDataAndUpdate();
  }

  af_componentRoot(children?: any): AppFormer.Element {
    return <MyReactComponent exposing={self => (this.screen = self)} />;
  }
}

AppFormer.registerScreen(new MyScreen());
```

```typescript jsx
import * as React from "react";
import * as AppFormer from "appformer-js";

export class MyPerspective extends AppFormer.Perspective {
  private perspective: MyReactComponent;

  constructor() {
    super("my-screen");
    this.af_isReact = true;
  }

  af_onOpen(): void {
    this.perspective.fetchDataAndUpdate();
  }

  af_componentRoot(children?: any): AppFormer.Element {
    return <MyReactComponent exposing={self => (this.perspective = self)} />;
  }
}

AppFormer.registerPerspective(new MyPerspective());
```

Note how AppFormer's lifecycle integrates with React's.