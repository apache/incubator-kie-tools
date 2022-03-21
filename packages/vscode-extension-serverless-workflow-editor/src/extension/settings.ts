import { settingsTokenKeys } from "@kie-tools-core/vscode-extension/dist/SettingsInterpolation";
import * as vscode from "vscode";

export class SwfVsCodeExtensionSettings {
  public getSpecsDirPath() {
    return vscode.workspace
      .getConfiguration()
      .get("kogito.sw.specsStoragePath", `${settingsTokenKeys["${fileDirname}"]}/specs`);
  }

  public shouldReferenceServiceRegistryFunctionsWithUrls() {
    return vscode.workspace.getConfiguration().get("kogito.sw.shouldReferenceServiceRegistryFunctionsWithUrls", false);
  }
}
