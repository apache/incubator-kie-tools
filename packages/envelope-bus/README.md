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

# @kie-tools-core/envelope-bus

Enables components in different contexts to communicate between themselves using a low-level `postMessage` method, inspired by [Window.postMessage()](https://developer.mozilla.org/en-US/docs/Web/API/Window/postMessage) in a type-safe way.

Works with `iframe`s, Shared Workers, Electron apps, VS Code extensions, and browser extensions.

### Concepts

- **`Channel`**: The main context (a.k.a. App Shell) which instantiates one or many **`Envelopes`**.
- **`Envelope`**: The wrapping layer containing a component that runs inside an isolated sub-context.

Communication between **`Channels`** and **`Envelopes`** is bi-directional, and can be done in three ways:

1. \***\*Notifications\*\***
   - Simple method invocation with parameters. No response mechanism.
1. \***\*Requests\*\***
   - Like an async function call. Can send arguments and receive responses.
1. \***\*Shared Values\*\***
   - Values that exist in Channels and Envelopes at the same time. Kept in sync via a publish/subscribe mechanism.

One **`Channel`** can instantiate and communicate with multiple **`Envelopes`**, but one **`Envelope`** can only communicate with the **`Channel`** which instantiated it. (**`Channel`** --1..n--> **`Envelope`**)

> This package can be understood as [**_Multiplying Architecture_**](../../repo/MULTIPLYING_ARCHITECTURE.md)'s core, as components wrapped inside an **`Envelope`** can be considered to be Micro-frontends, given their well-defined APIs and isolated context.

---

### Usage

Given two API definitions for a **`Channel`** and an **`Envelope`**:

```ts
import { SharedValueProvider } from "@kie-tools-core/envelope-bus/dist/api";

interface ChannelApi {
  setFoo(foo: string): void;
  requestFoo(id: string): Promise<string>;
  foo(): SharedValueProvider<string>;
}

interface EnvelopeApi {
  init(origin: string): Promise<void>;
  setBar(bar: string): void;
  requestBar(id: string): Promise<string>;
  bar(): SharedValueProvider<string>;
}
```

#### @ Channel (E.g., main window, or App Shell)

```ts
import { EnvelopeServer } from "@kie-tools-core/envelope-bus/dist/channel";

// Implement the Channel API.
// This is provided by the Channel and consumed by the Envelope.
const channelApiImpl: ChannelApi = {
  setFoo: (foo) => {
    console.log(`setFoo: ${foo}`);
  },
  requestFoo: async (id) => {
    return `fooId: ${id}`;
  },
  foo: () => {
    return { defaultValue: "default-foo" };
  },
};

// Create an EnvelopeServer
const envelopeServer = new EnvelopeServer<ChannelApi, EnvelopeApi>(
  {
    postMessage: (msg, targetOrigin) => {
      iframe.contentWindow?.postMessage(msg, targetOrigin);
    },
  },
  "my-origin",
  (self) => self.envelopeApi.requests.init(self.origin) // This function is called by `startInitPolling`
);

// Establish a connection with the Envelope
envelopeServer.startInitPolling(channelApiImpl);

// Once init polling finishes, you can start communicating with the Envelope by
// sending Notifications
envelopeServer.envelopeApi.notifications.setBar.send("bar");
// making Requests
envelopeServer.envelopeApi.requests.requestBar("1").then(console.log);
// or updating a Shared Value
envelopeServer.envelopeApi.shared.bar.set("bar");
```

> See [readme.test.ts](./tests/readme/readme.test.ts) for more details on subscribing and unsubscribing to Notifications and Shared Value updates.

#### @ Envelope (E.g., inside an iframe)

```ts
import { EnvelopeClient } from "@kie-tools-core/envelope-bus/dist/envelope";

const envelopeApiImpl: EnvelopeApi = {
  init: async (origin) => envelopeClient.associate(origin, envelopeServer.id),
  setBar: jest.fn(),
  requestBar: async (id) => `barId: ${id}`,
  bar: () => ({ defaultValue: "default-bar" }),
};

const envelopeClient = new EnvelopeClient<EnvelopeApi, ChannelApi>({
  postMessage: (msg, targetOrigin) => {
    return window.parent.postMessage(msg, targetOrigin);
  },
});

// Will start listening to "message" events in the window.
// Now the Envelope can start receiving messages from the Channel.
envelopeClient.startListening();

// Once listening is turned on, you can start communicating with the Channel by
// sending Notifications
envelopeClient.channelApi.notifications.setFoo.send("foo");
// making Requests
envelopeClient.channelApi.requests.requestFoo("1");
// or updating a Shared Value
envelopeClient.channelApi.shared.foo.set("foo");

// Will stop listening to "message" events in the window.
envelopeClient.stopListening();
```

> See [readme.test.ts](./tests/readme/readme.test.ts) for more details on subscribing and unsubscribing to Notifications and Shared Value updates.

---

### Usage in React

- Refer to [`Hooks.ts`](./src/hooks/Hooks.ts) for some convenience Hooks for easily using [**_Multiplying Architecture_**](../../repo/MULTIPLYING_ARCHITECTURE.md) concepts inside React components and applications. Such as:

- `useSharedValue`; and
- `useSubscription`

---

### References

- [**US20230009811A1** - COMMUNICATION SYSTEM FOR MICRO-FRONTENDS OF A WEB APPLICATION](https://patents.google.com/patent/US20230009811)
- [**US20230297444A1** - SYNCHRONIZING VARIABLE VALUES BETWEEN AN APPLICATION SHELL AND MICRO-FRONTENDS OF A WEB APPLICATION](https://patents.google.com/patent/US20230297444)

---

## For development information see:

- 👉 [DEV.md](./docs/DEV.md)
- 👉 [TESTS.md](./docs/TESTS.md)
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
