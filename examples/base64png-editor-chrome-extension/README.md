## Base64 PNG Editor - Chrome Extension

You can read [here](https://blog.kie.org/2020/10/kogito-tooling-examples%e2%80%8a-%e2%80%8ahow-to-create-a-chrome-extension-for-a-custom-editor.html) a step-by-step tutorial of how this Chrome Extension was built.

This package is the Chrome Extension, which runs the Base64 PNG Editor.

### Running

Install all dependencies and build the project on the **root** folder of this repository

```shell script
pnpm build:prod
```

Open your Chrome browser on the `chrome://extension`, choose to Load Unpacked, and select this package `dist/` folder

**Important**: In order to the extension works, it's necessary to run the command after you have build the application:

```
pnpm serve-envelope
```

It will run a server exposing your `dist/` folder on the `localhost:9000`. It's necessary to access your `localhost:9000`, and enable access to it.

This step is required, so the extension can access the envelope located on `dist/envelope/index.html`.
