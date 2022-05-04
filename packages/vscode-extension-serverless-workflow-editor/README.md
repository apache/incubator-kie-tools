## Serverless Workflow Editor

![vs-code-support](https://img.shields.io/badge/Visual%20Studio%20Code-1.46.0+-blue.svg)
![github-ci](https://github.com/kiegroup/kie-tools/actions/workflows/monorepo_pr_ci_full.yml/badge.svg)

Create and edit Serverless Workflow definition files (\*.sw.json, \*.sw.yaml, \*.sw.yml).

## Features

- Create and edit CNCF Serverless Workflow v0.8 definition files
- Real-time diagram preview rendering
- Editor code-lens to assist the authoring of your workflow
- Contextual auto-complete
- Open API auto-completion to your functions
- Integration with Red Hat Hybrid Console Authentication
- Red Hat Service Catalog Integration
- Automatically export diagram to SVG (see below for Settings)

#### Editing a new Serverless Workflow file

![alt](./gifs/sw.gif?raw=true)

### Settings

| Setting                                                       | Description                                                                                                                                                                                                                                             | Default value                                                                    |
| ------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------- |
| `kogito.sw.runOnSave`                                         | Execute a command on each save operation of the SW file                                                                                                                                                                                                 | `extension.kogito.swf.silentlyGenerateSvg`                                       |
| `kogito.sw.svgFilenameTemplate`                               | Filename template to be used when generating SVG files                                                                                                                                                                                                  | `${fileBasenameNoExtension}-svg.svg`                                             |
| `kogito.sw.svgFilePath`                                       | Where to save generated SVG files                                                                                                                                                                                                                       | `${fileDirname}`                                                                 |
| `kogito.sw.specsStoragePath`                                  | Directory where OpenAPI spec files are stored (defaults to a 'specs' directory in the same path as the Serverless Workflow file).                                                                                                                       | `${fileDirname}/specs`                                                           |
| `kogito.sw.serviceRegistryUrl`                                | The Service Registry URL to fetch artifacts that improve the functions autocompletion mechanism.                                                                                                                                                        | `(empty)`                                                                        |
| `kogito.sw.shouldReferenceServiceRegistryFunctionsWithUrls`   | When adding a function coming from a Service Registry, use its URL to reference it, instead of downloading the OpenAPI file.                                                                                                                            | `false`                                                                          |
| `kogito.sw.automaticallyOpenDiagramEditorAlongsideTextEditor` | When opening Serverless Workflow files, decide whether or not to open the Diagram Editor alongside the text editor. Regardless of the configured option, you can always open the Serverless Workflow Diagram Editor using the 'Open as Diagram' button. | `Ask next time` (possible: `Open automatically`, `Do not open`, `Ask next time`) |

The `kogito.sw.svgFilenameTemplate`, `kogito.sw.svgFilePath`, and `kogito.sw.specsStoragePath` settings accept the following variables as tokens:

| Variable                       | Example                                   |
| ------------------------------ | ----------------------------------------- |
| **${workspaceFolder}**         | `/home/your-username/your-project`        |
| **${fileDirname}**             | `/home/your-username/your-project/folder` |
| **${fileExtname}**             | `.ext`                                    |
| **${fileBasename}**            | `file.ext`                                |
| **${fileBasenameNoExtension}** | `file`                                    |

### Thank you

Some Open Source projects were vital for the development of this extension, and we would like to thank all of them, with highlights to:

- [CNCF Serverless Workflow SDK Typescript](https://github.com/serverlessworkflow/sdk-typescript): for workflow parsing;
- [Apicurio](https://www.apicur.io/): for our service catalog integration;

You can also checkout the [CNCF Serverless Workflow VSCode extension](https://marketplace.visualstudio.com/items?itemName=serverlessworkflow.serverless-workflow-vscode-extension) at VSCode Marketplace.
