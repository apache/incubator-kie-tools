## BPMN Editor

![vs-code-support](https://img.shields.io/badge/Visual%20Studio%20Code-1.67.0+-blue.svg)
![github-ci](https://github.com/apache/incubator-kie-tools/actions/workflows/ci_build.yml/badge.svg)

Create and edit BPMN and BPMN2 files.

## Features

- Create and edit BPMN (`.bpmn`) and BPMN2 (`.bpmn2`) files.
- Native keyboard shortcuts (Press `shift+/` to display available combinations).
- Export diagram to SVG (use the SVG icon on the top-right corner).

### Editing a new BPMN file

![alt](./gifs/bpmn.gif?raw=true)

### Settings

| Setting                           | Description                                               | Default value                        |
| --------------------------------- | --------------------------------------------------------- | ------------------------------------ |
| `kogito.bpmn.runOnSave`           | Execute a command on each save operation of the BPMN file | _empty_                              |
| `kogito.bpmn.svgFilenameTemplate` | Filename template to be used when generating SVG files    | `${fileBasenameNoExtension}-svg.svg` |
| `kogito.bpmn.svgFilePath`         | Where to save generated SVG files                         | `${fileDirname}`                     |

The `kogito.bpmn.svgFilenameTemplate` and `kogito.bpmn.svgFilePath` settings accept the following variables as tokens:

| Variable                       | Example                                   |
| ------------------------------ | ----------------------------------------- |
| **${workspaceFolder}**         | `/home/your-username/your-project`        |
| **${fileDirname}**             | `/home/your-username/your-project/folder` |
| **${fileExtname}**             | `.ext`                                    |
| **${fileBasename}**            | `file.ext`                                |
| **${fileBasenameNoExtension}** | `file`                                    |
