# 'To do' list View :: VS Code Extension

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
   
Building
--
Run `yarn run build:prod`. A `.vsix` file will be on the `dist` folder.
