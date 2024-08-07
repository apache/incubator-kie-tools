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

# Ping-Pong View

You can read [here](https://blog.kie.org/2020/10/kogito-tooling-examples-how-to-create-a-more-complex-custom-view.html) a step-by-step tutorial of how this custom View was built.

This package exposes the necessary files for you to create a Ping-Pong View Envelope with your Ping-Pong View implementation.

It's divided into the following submodules:

1. `api`
   - Provides the APIs that the Channel/Envelope expose to each other.
2. `embedded`
   - Provides a convenient React component to embed a Ping-Pong View in a Web application.
3. `envelope`
   - Provides the necessary class for a Channel to create a Ping-Pong View Envelope.

## How to create your implementation

1. **Factory implementation**
   - Create a class that implements the `PingPongFactory` interface;
   - The `create` method should receive the initial arguments and the channel API to be used in your implementation;
   - The `create` method also should return a function that returns the implementation of the `PingPongApi`, with methods to `clearLogs` and `getLastPingTimestamp`;
   - From there you can do what fits best for your project to subscribe and receive the notifications from the channel API.
2. **API Implementation**
   - Create a class that implements the `PingPongEnvelopeApi` interface or use the one provided in `envelope/PingPongEnvelopeApiImpl.ts`;
   - The `pingPongView__init` will be responsible for associating the `envelopeClient` with the `envelopeServer` and then calling the `create` method from your factory implementation;
   - From the `create` call you should get the `PingPongApi` implementation and use it to fulfill the `PingPongEnvelopeApi` methods.
3. **Running inside an `EmbeddedEnvelope`**
   - Your project can run inside a `div` or an `iFrame`, and for that, both are available inside the `embedded` directory.
   - Both require similar props and these are the ones that are common to them:
     ```js
     apiImpl: PingPongChannelApi; // The API implementation
     targetOrigin: string; // The current location origin url
     name: string; // The envelope name
     ```
   - For **div** the `renderView` prop is required:
     ```js
     renderView: (container: HTMLDivElement, envelopeId?: string) => Promise<void>;
     ```
     It's responsible for rendering the Ping Pong view inside the `container` element and passing the `envelopeId` to be used when initializing the envelope.
   - For **iFrame** the `envelopePath` prop is required:
     ```
     envelopePath: string;
     ```
     It should receive the URL to be loaded inside the iFrame.
4. **Initializing**

   - When loading your ping pong implementation call the `init` method from the `PingPongEnvelope` class
   - It should receive the following arguments:

     ```js
     {
       config: EnvelopeDivConfig | EnvelopeIFrameConfig;
       bus: EnvelopeBus;
       pingPongViewFactory: PingPongFactory;
     }
     ```

     - **config** is one of `EnvelopeDivConfig` or `EnvelopeIFrameConfig`, specifying if the envelope is running in an _iFrame_ or a _div_, passing the `envelopeId` if in a _div_;
     - **bus** is the medium through which messages will be transmitted/received. By default we use:
       ```js
       {
         postMessage: (message, _targetOrigin, transfer) => window.parent.postMessage(message, "*", transfer);
       }
       ```
     - **pingPongViewFactory** should be an instance of the factory class created in step _1_;

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
