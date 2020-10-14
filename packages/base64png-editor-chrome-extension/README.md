## Base64 PNG Editor - Chrome Extension

This package is the Chrome Extension, which runs the Base64 PNG Editor.

### Running

Install all dependencies and build the project on the **root** folder of this repository

```shell script
yarn run init
yarn build:prod
```

Open your Chrome browser on the `chrome://extension`, choose to Load Unpacked, and select this package `dist/` folder

**Important**: In order to the extension works, it's necessary to run the command after you have build the application:

```
yarn serve-envelope
```

It will run a server exposing your `dist/` folder on the `localhost:9000`. It's necessary to access your `localhost:9000`, and enable access to it.

This step is required, so the extension can access the envelope located on `dist/envelope/index.html`.
