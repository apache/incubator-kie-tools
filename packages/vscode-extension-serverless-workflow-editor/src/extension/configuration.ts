import {
  configurationTokenKeys,
  getInterpolatedConfigurationValue,
} from "@kie-tools-core/vscode-extension/dist/ConfigurationInterpolation";
import * as vscode from "vscode";

export const CONFIGURATION_SECTIONS = {
  serviceRegistryUrl: "kogito.sw.serviceRegistryUrl",
  specsStoragePath: "kogito.sw.specsStoragePath",
  shouldReferenceServiceRegistryFunctionsWithUrls: "kogito.sw.shouldReferenceServiceRegistryFunctionsWithUrls",
};

export class SwfVsCodeExtensionConfiguration {
  public getConfiguredSpecsDirPath() {
    return vscode.workspace
      .getConfiguration()
      .get(CONFIGURATION_SECTIONS.specsStoragePath, `${configurationTokenKeys["${fileDirname}"]}/specs`);
  }

  public getConfiguredFlagShouldReferenceServiceRegistryFunctionsWithUrls() {
    return vscode.workspace
      .getConfiguration()
      .get(CONFIGURATION_SECTIONS.shouldReferenceServiceRegistryFunctionsWithUrls, false);
  }

  public getConfiguredServiceRegistryUrl() {
    return vscode.workspace.getConfiguration().get(CONFIGURATION_SECTIONS.serviceRegistryUrl, "");
  }

  public getInterpolatedSpecsDirAbsolutePosixPath(args: { baseFileAbsolutePosixPath: string }) {
    return getInterpolatedConfigurationValue({
      currentFileAbsolutePosixPath: args.baseFileAbsolutePosixPath,
      value: this.getConfiguredSpecsDirPath(),
    });
  }
}
