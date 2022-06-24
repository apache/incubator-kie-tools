## yard Editor

![vs-code-support](https://img.shields.io/badge/Visual%20Studio%20Code-1.46.0+-blue.svg)
![github-ci](https://github.com/kiegroup/kie-tools/actions/workflows/monorepo_pr_ci_full.yml/badge.svg)

Create and edit yard (Yet Another Rule Definition) files (\*.yard.yaml, \*.yard.yml, \*.yard.json).

## Features

- Create and edit yard definition files
- Automatically export diagram to SVG (see below for Settings)

### Settings

| Setting                                                        | Description                                                                                                                                                                                                                                             | Default value                                                                    |
| -------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------- |
| `kogito.yard.runOnSave`                                        | Execute a command on each save operation of the yard file                                                                                                                                                                                               | `extension.kogito.yard.silentlyGenerateSvg`                                      |
| `kogito.yard.svgFilenameTemplate`                              | Filename template to be used when generating SVG files                                                                                                                                                                                                  | `${fileBasenameNoExtension}-svg.svg`                                             |
| `kogito.yard.svgFilePath`                                      | Where to save generated SVG files                                                                                                                                                                                                                       | `${fileDirname}`                                                                 |
| `kogito.swf.automaticallyOpenDiagramEditorAlongsideTextEditor` | When opening Serverless Workflow files, decide whether or not to open the Diagram Editor alongside the text editor. Regardless of the configured option, you can always open the Serverless Workflow Diagram Editor using the 'Open as Diagram' button. | `Ask next time` (possible: `Open automatically`, `Do not open`, `Ask next time`) |

The `kogito.yard.svgFilenameTemplate` and `kogito.yard.svgFilePath` settings accept the following variables as tokens:

| Variable                       | Example                                   |
| ------------------------------ | ----------------------------------------- |
| **${workspaceFolder}**         | `/home/your-username/your-project`        |
| **${fileDirname}**             | `/home/your-username/your-project/folder` |
| **${fileExtname}**             | `.ext`                                    |
| **${fileBasename}**            | `file.ext`                                |
| **${fileBasenameNoExtension}** | `file`                                    |
