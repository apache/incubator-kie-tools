## Kogito yard Editor

![vs-code-support](https://img.shields.io/badge/Visual%20Studio%20Code-1.67.0+-blue.svg)
![github-ci](https://github.com/apache/incubator-kie-tools/actions/workflows/ci_build.yml/badge.svg)

Create and edit yard (Yet Another Rule Definition) files (\*.yard.yaml, \*.yard.yml).

## Features

- Create and edit yard definition files

### Settings

| Setting                                                         | Description                                                                                                                                                                                                               | Default value                                                                    |
| --------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------- |
| `kogito.yard.runOnSave`                                         | Execute a command on each save operation of the yard file                                                                                                                                                                 | _empty_                                                                          |
| `kogito.yard.automaticallyOpenDiagramEditorAlongsideTextEditor` | When opening yard files, decide whether or not to open the Diagram Editor alongside the text editor. Regardless of the configured option, you can always open the yard Diagram Editor using the 'Open as Diagram' button. | `Ask next time` (possible: `Open automatically`, `Do not open`, `Ask next time`) |
