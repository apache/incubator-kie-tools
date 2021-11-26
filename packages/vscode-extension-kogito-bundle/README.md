## Kogito Bundle

![vs-code-support](https://img.shields.io/badge/Visual%20Studio%20Code-1.46.0+-blue.svg)
![github-ci](https://github.com/kiegroup/kogito-tooling/actions/workflows/monorepo_pr_ci_full.yml/badge.svg)

Create and edit BPMN, DMN and SceSim files.

## Features

- Create and edit BPMN (`.bpmn`) and BPMN2 (`.bpmn2`) files.
- Create and edit DMN 1.1 and DMN 1.2 (`.dmn`) files.
- Create and edit SceSim (`.scesim`) files with the Test Scenario Editor.
- Native keyboard shortcuts (Press `shift+/` to display available combinations).
- Export diagram to SVG (use the SVG icon on the top-right corner).

### Editing a new BPMN file

![alt](./gifs/bpmn.gif?raw=true)

### Editing a new DMN file

![alt](./gifs/dmn.gif?raw=true)

### Settings

| Setting                        | Description                                               | Default value                        |
| ------------------------------ | --------------------------------------------------------- | ------------------------------------ |
| `kogito.bpmn.runOnSave`        | Execute a command on each save operation of the BPMN file | _empty_                              |
| `kogito.dmn.runOnSave`         | Execute a command on each save operation of the DMN file  | _empty_                              |
| `kogito.bpmn.filenameTemplate` | Filename template to be used when generating SVG files    | `${fileBasenameNoExtension}-svg.svg` |
| `kogito.dmn.filenameTemplate`  | Filename template to be used when generating SVG files    | `${fileBasenameNoExtension}-svg.svg` |
| `kogito.bpmn.filePath`         | Where to save generated SVG files                         | `${fileDirname}`                     |
| `kogito.dmn.filePath`          | Where to save generated SVG files                         | `${fileDirname}`                     |

The `kogito.{bpmn|dmn}.filenameTemplate` and `kogito.{bpmn|dmn}.filePath` settings accept the following variables as tokens:

| Variable                       | Example                                   |
| ------------------------------ | ----------------------------------------- |
| **${workspaceFolder}**         | `/home/your-username/your-project`        |
| **${fileDirname}**             | `/home/your-username/your-project/folder` |
| **${fileExtname}**             | `.ext`                                    |
| **${fileBasename}**            | `file.ext`                                |
| **${fileBasenameNoExtension}** | `file`                                    |
