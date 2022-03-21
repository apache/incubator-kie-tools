import {
  getInterpolateSettingsValue,
  settingsTokenKeys,
} from "@kie-tools-core/vscode-extension/dist/SettingsInterpolation";
import * as vscode from "vscode";

export const CONFIGURATION_SECTIONS = {
  serviceRegistryUrl: "kogito.sw.serviceRegistryUrl",
  specsStoragePath: "kogito.sw.specsStoragePath",
  shouldReferenceServiceRegistryFunctionsWithUrls: "kogito.sw.shouldReferenceServiceRegistryFunctionsWithUrls",
};

export class SwfVsCodeExtensionSettings {
  public getSpecsDirPath() {
    return vscode.workspace
      .getConfiguration()
      .get(CONFIGURATION_SECTIONS.specsStoragePath, `${settingsTokenKeys["${fileDirname}"]}/specs`);
  }

  public shouldReferenceServiceRegistryFunctionsWithUrls() {
    return vscode.workspace
      .getConfiguration()
      .get(CONFIGURATION_SECTIONS.shouldReferenceServiceRegistryFunctionsWithUrls, false);
  }

  public getServiceRegistryUrl() {
    return vscode.workspace.getConfiguration().get(CONFIGURATION_SECTIONS.serviceRegistryUrl, "");
  }

  public getInterpolatedSpecsDirPath(args: { baseFileAbsolutePath: string }) {
    return getInterpolateSettingsValue({
      currentFileAbsolutePath: args.baseFileAbsolutePath,
      value: this.getSpecsDirPath(),
    });
  }
}
