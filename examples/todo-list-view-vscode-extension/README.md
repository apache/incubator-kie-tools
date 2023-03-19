# 'To do' list View :: VS Code Extension

You can read [here](https://blog.kie.org/2020/10/kogito-tooling-examples%e2%80%8a-%e2%80%8ahow-to-create-a-vs-code-extension-for-a-custom-view.html) a step-by-step tutorial of how this VS Code Extension was built.

This package provides a VS Code Extension containing a 'To do' list View.

### Pre requisites

This example requires the VS Code version 1.46 or later.

This extensions has the following commands:

1. `TODO: Open list`
   - Opens a Webview with the 'To do' list View.
1. `TODO: Add item(s)`
   - When there's a selected text in a text editor (e.g. `txt`) you can right-click it to add the selection as a 'To do' item.
1. `TODO: Mark all as completed`
   - Marks all the items in the 'To do' list as completed.

## Building

Run `pnpm build:prod`. A `.vsix` file will be on the `dist` folder.
