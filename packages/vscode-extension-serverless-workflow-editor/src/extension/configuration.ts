import {
  getInterpolatedConfigurationValue,
  configurationTokenKeys,
} from "@kie-tools-core/vscode-extension/dist/ConfigurationInterpolation";
import * as vscode from "vscode";

export const CONFIGURATION_SECTIONS = {
  serviceRegistryUrl: "kogito.sw.serviceRegistryUrl",
  specsStoragePath: "kogito.sw.specsStoragePath",
  shouldReferenceServiceRegistryFunctionsWithUrls: "kogito.sw.shouldReferenceServiceRegistryFunctionsWithUrls",
};

export class SwfVsCodeExtensionConfiguration {
  public getSpecsDirPath() {
    return vscode.workspace
      .getConfiguration()
      .get(CONFIGURATION_SECTIONS.specsStoragePath, `${configurationTokenKeys["${fileDirname}"]}/specs`);
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
    return getInterpolatedConfigurationValue({
      currentFileAbsolutePath: args.baseFileAbsolutePath,
      value: this.getSpecsDirPath(),
    });
  }
}
