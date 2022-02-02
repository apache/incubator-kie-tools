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
