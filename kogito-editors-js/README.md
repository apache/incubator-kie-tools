# Kogito editors JS

**Kogito editors JS** module holds the next generation of JavaScript/TypeScript/React components for the **BPMN**, **DMN**, and **Scenario Simulation** editors.

It is, in essence, an NPM module with dependencies managed by `package.json`. Still, the `pom.xml` is present to wrap static bundles into a web jar consumable by GWT editors.

## Requirements

- [Maven](https://maven.apache.org/) 3.6.2 or later
- [Java](https://openjdk.java.net/install/) 11 or later
- [Node](https://nodejs.org) 16.2.0
- [Yarn](https://yarnpkg.com/getting-started/install) 1.22.10

## Build

Maven runs Yarn to build production assets and packs everything in a jar:

```bash
mvn clean install
```

### Yarn build

You may trigger Yarn build manually by using development and production mode:

```bash
# Development
yarn build:dev

# Production (used by Maven)
yarn build:prod
```

### Prettier

You may format the code with Prettier on your IDE or even by running:

```bash
yarn format
```

## Development

Follow these guides to develop React components with GWT editors smoothly:

- [How to create a Kogito editor component](./docs/new-component.md)
- [How to Wire a Kogito editor components with a GWT](./docs/wire.md)

## Contributing

All contributions are welcome! Before you start, please read the [contribution guide](../CONTRIBUTING.md).
