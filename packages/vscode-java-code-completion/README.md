# Apache KIE Tools Java Code Completion

This package provides a type-safe Java Code Completion library for a Typescript project.

## Install

- `npm install @kie-tools-core/vscode-java-code-completion`

## Usage

The library is separated into two submodules:

- api
  All the APIs and the main classes needed are in this submodule.

  to use the core:

  - `import { JavaCodeCompletionApi } from "@kie-tools-core/vscode-java-code-completion/dist/api"`
  - `import { JavaCodeCompletionAccessor } from "@kie-tools-core/vscode-java-code-completion/dist/api"`
  - `import { JavaCodeCompletionClass } from "@kie-tools-core/vscode-java-code-completion/dist/api"`

- vscode

  All the classes needed to use in vscode channel implementation

  to use the vscode classes:

  ```ts
  import { JavaCodeCompletionApi } from "@kie-tools-core/vscode-java-code-completion/dist/api";
  import { VsCodeJavaCodeCompletionApiImpl } from "@kie-tools-core/vscode-java-code-completion/dist/vscode";

  const api: JavaCodeCompletionApi = new VsCodeJavaCodeCompletionApiImpl();
  ```

## API

- `VsCodeJavaCodeCompletionApi.getAccessors(fqcn:string,query:string): JavaCodeCompletionAccessor[]`: Receives the FQCN and a query to search in that
- `VsCodeJavaCodeCompletionApi.getClasses(query:string): JavaCodeCompletionClass[]`: Receives the query to search classes in project classpath
- `VsCodeJavaCodeCompletionApi.isJavaLanguageServerAvailable(): boolean`: Returns true if the server is ready to be used.
