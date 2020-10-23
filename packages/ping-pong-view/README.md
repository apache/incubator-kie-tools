# Ping-Pong View

You can read [here](https://blog.kie.org/2020/10/kogito-tooling-examples-how-to-create-a-more-complex-custom-view.html) a step-by-step tutorial of how this custom View was built.

This package exposes the necessary files for you to create a Ping-Pong View Envelope with you own Ping-Pong View implementation.

It's divided in the following submodules:

1. `api`
   - Provides the APIs that the Channel/Envelope expose to each other.
1. `embedded`
   - Provides a convenience React component to embed a Ping-Pong View in a Web application.
1. `envelope`
   - Provides the necessary class for a Channel to create a Ping-Pong View Envelope.
