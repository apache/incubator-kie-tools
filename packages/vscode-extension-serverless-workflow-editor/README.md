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

| Setting                         | Description                                             | Default value                              |
| ------------------------------- | ------------------------------------------------------- | ------------------------------------------ |
| `kogito.sw.runOnSave`           | Execute a command on each save operation of the SW file | `extension.kogito.swf.silentlyGenerateSvg` |
| `kogito.sw.svgFilenameTemplate` | Filename template to be used when generating SVG files  | `${fileBasenameNoExtension}-svg.svg`       |
| `kogito.sw.svgFilePath`         | Where to save generated SVG files                       | `${fileDirname}`                           |

The `kogito.sw.svgFilenameTemplate` and `kogito.sw.svgFilePath` settings accept the following variables as tokens:

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
- [Mermaid](https://mermaid-js.github.io/): for workflow rendering and visualization;
- [Apicurio](https://www.apicur.io/): for our service catalog integration;

You can also checkout the [CNCF Serverless Workflow VSCode extension](https://marketplace.visualstudio.com/items?itemName=serverlessworkflow.serverless-workflow-vscode-extension) at VSCode Marketplace.
