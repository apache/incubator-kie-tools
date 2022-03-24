import * as vscode from "vscode";

export async function askForServiceRegistryUrl(args: { currentValue: string | undefined }) {
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
          return "Error: The provided input is not a valid Service Registry URL.";
        }
      },
    })
    .then((urlString) => {
      if (!urlString) {
        return;
      }
      return urlString;
    });
}

export function getServiceFileNameFromSwfServiceCatalogServiceId(swfServiceCatalogServiceId: string) {
  return `${swfServiceCatalogServiceId}__latest.yaml`;
}
