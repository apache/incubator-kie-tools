# LSP Extension Plugin

## Installation

This project extends the JDT.LS by providing an extension point for `org.eclipse.jdt.ls.core.delegateCommandHandler`.

This project is built using [Eclipse Tycho](https://www.eclipse.org/tycho/) and requires at least [maven 3.0](http://maven.apache.org/download.html) to be built via CLI.

Simply run :

    mvn install

The first run will take quite a while since maven will download all the required dependencies in order to build everything.

## Usage

Once compiled you need to copy the generated JAR in a folder inside the extension, and you need to configure that location path in _contributes_ section in pacakge.json

```json
"contributes": {
    "javaExtensions": [
      "./server/vscode-lsp-extension-plugin.jar"
    ],
    ...
}
```

and you also need `redhat.java` as extension dependency:

```json
 "extensionDependencies": [
    "redhat.java"
  ]
```

Once done that, the Language Server will automatically recognize the new plugin.
