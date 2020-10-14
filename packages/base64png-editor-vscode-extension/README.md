## Base64 PNG Editor - Visual Studio Code Extension

This package is the VS Code Extension, which runs the Base64 PNG Editor.

### Pre requisites
This example requires the VS Code version 1.46 or later.

### Running
Install all dependencies and build the project on the **root** folder of this repository
```shell script
yarn run init
yarn build:prod
```

- Debug mode
```shell script
// open this package with visual studio code
code .
// press F5 and wait to start
```
- Installing VSIX

The `yarn build:prod` generate a vsix file on the `/dist` folder. Open your VS Code, and install it.