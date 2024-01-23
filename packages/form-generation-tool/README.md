## Form Generation Tool

This is a utility CLI to help generating forms in differents formats for BPMN processes and User Tasks in your Kogito projects.

### How does it work?

When building a Kogito project, Kogito generates JSON schemas to represent the data models for both Processes and User Tasks.

This tool locates those JSON Schemas in the project and taking advantage of the [Uniforms](https://uniforms.tools) APIs, processes them and generate static forms as a resources in the project `src/main/resoures/form` folder.

### Form Types

There are two types of form supported with differnt styling and output format:

- **Patternfly**: generates a React (`.tsx`) forms using Patternfly 4 components. Implementation can be found in `@kie-tools/uniforms-patternfly-codegen`

- **Bootstrap 4**: generates a HTML (`.html`) forms using Bootstrap 4 styling.

Each generated form consist in two files, the source code (`.tsx` or `.html`) and a companion `.config` file that defines the external resources (`css` / `js`) the form may need.

## Build

In order to build the CLI you must run the following command in the root folder of the repository:

```shell script
pnpm -F @kie-tools/form-generation-tool... build:prod
```

After the command has finished, go to `packages/form-generation-tool/dist` folder and you'll find the CLI binary (`form-generation-cli-macos`, `form-generation-cli-linux` or `form-generation-cli-win.exe` depending on your OS).

## Running the CLI

If you built the `form-generation-tool` package as described above locally, then in the command line just execute the CLI binary:

```shell script
./form-generation-cli-linux
```

For those, who do not want to build `form-generation-tool` package locally, they can install last published version and then run it:

```shell script
npm i -g @kie-tools/form-generation-tool
form-generation-tool
```

For those, who want to try `form-generation-tool` without instalation they can start it as:

```shell script
npx @kie-tools/form-generation-tool
```

All commands will start a wizard to help you generate the forms:

1. First set the path to your Kogito Project.

![Step 1: Set the Kogito Project path](./docs/form-generation-1.png)

> **ℹ️ NOTE:** Make sure your Kogito project has been compiled, otherwhise the CLI won't be able to find all the form schemas.

2. Select one of the availables the Form types (Patternfly or Bootstrap 4).

![Step 2: Select the Form type](./docs/form-generation-2.png)

3. Confirm Selection to start the Form Generation process.

![Step 3: Select the Form type](./docs/form-generation-3.png)

4. If the Form generation process is succesful, you'll get a confirmation message.

![Form generation succesfully finished](./docs/form-generation-4.png)

The generated forms will be stored in the `src/main/resources/forms` folder of the Kogito project.

![List of generated forms in project](./docs/form-generation-5.png)

## Using the Custom Forms with Runtime Tools Quarkus Extension

If your project is a Quarkus based Kogito project, you can use and test them by using the **Runtime Tools Quarkus Extension**.

To do so, just add the following dependency in your project `pom.xml`:

```xml
<dependency>
    <groupId>org.kie.kogito</groupId>
    <artifactId>runtime-tools-quarkus-extension</artifactId>
    <version>${version}</version>
</dependency>
```

And start the project in Dev mode with the command:

```shell script
mvn clean quarkus:dev
```

For more information on how to setup the **Runtime Tools Quarkus Extension** in your project look at the oficial Kogito [documentation](https://docs.kogito.kie.org/latest/html_single/#con-runtime-tools-dev-ui_kogito-developing-process-services).
