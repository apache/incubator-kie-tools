# 'To do' list View

You can read [here](https://blog.kie.org/2020/10/kogito-tooling-examples-how-to-create-a-custom-view.html) a step-by-step tutorial of how this custom View was built.

This package exposes the necessary files for you to create a 'To do' list Envelope.

It's divided in the following submodules:

1. `api`
   - Provides the APIs that the Channel/Envelope expose to each other.
1. `embedded`
   - Provides a convenience React component to embed a 'To do' list View in a Web application.
1. `envelope`
   - Provides the necessary class for a Channel to create a 'To do' list Envelope.
1. `vscode`
   - Provides a convenience class to create a Webview inside a VS Code Extension.
