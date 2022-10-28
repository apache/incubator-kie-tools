## DMN Editor

![vs-code-support](https://img.shields.io/badge/Visual%20Studio%20Code-1.66.0+-blue.svg)
![github-ci](https://github.com/kiegroup/kie-tools/actions/workflows/ci_build.yml/badge.svg)

Create and edit DMN and SceSim files.

## Features

- Create and edit DMN 1.1 and DMN 1.2 (`.dmn`) files.
- Create and edit SceSim (`.scesim`) files with the Test Scenario Editor.
- Native keyboard shortcuts (Press `shift+/` to display available combinations).
- Export diagram to SVG (use the SVG icon on the top-right corner).

### Editing a new DMN file

![alt](./gifs/dmn.gif?raw=true)

### Editing a SceSim file

DMN files must be inside a `src/` folder on your Workspace to be visible on the Test Scenario Editor.

![alt](./gifs/scesim.gif?raw=true)

### Settings

| Setting                          | Description                                              | Default value                        |
| -------------------------------- | -------------------------------------------------------- | ------------------------------------ |
| `kogito.dmn.runOnSave`           | Execute a command on each save operation of the DMN file | _empty_                              |
| `kogito.dmn.svgFilenameTemplate` | Filename template to be used when generating SVG files   | `${fileBasenameNoExtension}-svg.svg` |
| `kogito.dmn.svgFilePath`         | Where to save generated SVG files                        | `${fileDirname}`                     |

The `kogito.dmn.svgFilenameTemplate` and `kogito.dmn.svgFilePath` settings accept the following variables as tokens:

| Variable                       | Example                                   |
| ------------------------------ | ----------------------------------------- |
| **${workspaceFolder}**         | `/home/your-username/your-project`        |
| **${fileDirname}**             | `/home/your-username/your-project/folder` |
| **${fileExtname}**             | `.ext`                                    |
| **${fileBasename}**            | `file.ext`                                |
| **${fileBasenameNoExtension}** | `file`                                    |
