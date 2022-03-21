import * as vscode from "vscode";

export async function askForServiceRegistryUrl(args: { currentValue: URL | undefined }) {
  return vscode.window
    .showInputBox({
      ignoreFocusOut: true,
      title: "Serverless Workflow Editor",
      prompt:
        "Provide the Service Registry URL to import functions.\n\nThat's the 'Core Registry API' URL you see on the Connection menu inside the Service Registry instance.",
      value: args.currentValue?.toString(),
      valueSelection: undefined, // Select everything
      validateInput(value: string) {
        try {
          new URL(value);
          return undefined;
        } catch (e) {
          return "Must be a valid URL";
        }
      },
    })
    .then((urlString) => {
      if (!urlString) {
        return;
      }
      return new URL(urlString);
    });
}
