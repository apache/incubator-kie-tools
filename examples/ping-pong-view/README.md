# Ping-Pong View

You can read [here](https://blog.kie.org/2020/10/kogito-tooling-examples-how-to-create-a-more-complex-custom-view.html) a step-by-step tutorial of how this custom View was built.

This package exposes the necessary files for you to create a Ping-Pong View Envelope with you own Ping-Pong View implementation.

It's divided in the following submodules:

1. `api`
   - Provides the APIs that the Channel/Envelope expose to each other.
2. `embedded`
   - Provides a convenience React component to embed a Ping-Pong View in a Web application.
3. `envelope`
   - Provides the necessary class for a Channel to create a Ping-Pong View Envelope.

## How to create your own implementation

1. **Factory implementation**
   - Create a class that implements the `PingPongFactory` interface;
   - The `create` method should receive the initial arguments and the channel API to be used in your implementation.
   - From there you can do what fits best for your project to subscribe and receive the notifications from the channel API.
2. **API Implementation**
   - Create a class that implements the `PingPongEnvelopeApi` interface or use the one provided in `envelope/PingPongEnvelopeApiImpl.ts`;
   - The `pingPongView__init` will be responsible for associating the `envelopeClient` with the `envelopeServer`, waiting for the view to be ready (in our implementation via the async `viewDelegate` method) and then calling the `create` method from your factory implementation.
3. **Runing inside an `EmbeddedEnvelope`**
   - Your project can run inside a `div` or an `iFrame`, and for that both are available inside the `embedded` directory.
   - Both require similar props and these are the ones that are common to them:
     ```
     apiImpl: PingPongChannelApi; // The API implementation
     targetOrigin: string; // The current location origin url
     name: string; // The envelope name
     ```
   - For **div** the `renderView` prop is required:
     ```
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

     ```
     {
        config: EnvelopeDivConfig | EnvelopeIFrameConfig;
        bus: EnvelopeBus;
        pingPongViewFactory: PingPongFactory;
        viewReady: () => Promise<() => PingPongViewType>;
     }
     ```

     - **config** is one of `EnvelopeDivConfig` or `EnvelopeIFrameConfig`, specifying if the envelope is runnning in an _iFrame_ of a _div_, passing the `envelopeId` if in a _div_
     - **bus** is the medium through which messages will be transmitted/received. By default we use:
       ```
       { postMessage: (message, _targetOrigin, transfer) => window.parent.postMessage(message, "*", transfer) }
       ```
     - **pingPongViewFactory** should an instance of the factory class created in step _1_
     - **viewReady** is an async method that should resolve when the view is loaded and ready to start transmitting and receiving messages from the channel
