## Webapp Example

You can read [here](https://blog.kie.org/2020/10/kogito-tooling-examples%e2%80%8a-%e2%80%8ahow-to-integrate-a-custom-editor-an-existing-editors-and-custom-views.html) a step-by-step tutorial of how create this WebApp.

This is a Web application example that shows how to integrate an Embedded Editor [1] or an Embedded Envelope [2]

1. The Embedded Editor enables you to use your Custom Editors or even to use the available Kogito Editor (BPMN and DMN)
1. The Embedded Envelope gives you more flexibility to create any kind of application, in this example we bring two custom Views.

## Details

To get more the details please take a look on each implementation:

- [Base64 PNG Editor]("src/Pages/Base64Png/Base64PngPage.tsx")
- [BPMN Editor]("src/Pages/KogitoEditors/BpmnPage.tsx")
- [DMN Editor]("src/Pages/KogitoEditors/DmnPage.tsx")
- ['To-do' list View]("src/Pages/TodoList/TodoListViewPage.tsx")
- [Ping-Pong View]("src/Pages/PingPong/PingPongViewsPage.tsx")

## Setup

### Initialize

To install all dependencies it's necessary to execute the following command on the root folder of the project:

```shell script
pnpm init
```

### Build

To build the webapp execute one of the following commands on the root folder of the project:

```shell script
KIE_TOOLS_BUILD__buildExamples=true pnpm -F @kie-tools-examples/webapp... build:dev
KIE_TOOLS_BUILD__buildExamples=true pnpm -F @kie-tools-examples/webapp... build:prod
```

### Run

To start the webapp execute the following command on the root folder of the project:

```shell script
pnpm -F @kie-tools-examples/webapp start
```
