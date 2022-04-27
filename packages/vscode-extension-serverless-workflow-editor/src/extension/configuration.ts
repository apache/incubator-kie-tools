import {
  configurationTokenKeys,
  getInterpolatedConfigurationValue,
} from "@kie-tools-core/vscode-extension/dist/ConfigurationInterpolation";
import * as vscode from "vscode";

export const WEBVIEW_EDITOR_VIEW_TYPE = "kieKogitoWebviewEditorsServerlessWorkflow";

export const CONFIGURATION_SECTIONS = {
  serviceRegistryUrl: "kogito.sw.serviceRegistryUrl",
  specsStoragePath: "kogito.sw.specsStoragePath",
  shouldReferenceServiceRegistryFunctionsWithUrls: "kogito.sw.shouldReferenceServiceRegistryFunctionsWithUrls",
  automaticallyOpenDiagramEditorAlongsideTextEditor: "kogito.sw.automaticallyOpenDiagramEditorAlongsideTextEditor",
};

type ShouldOpenDiagramEditorAutomaticallyType = "Ask next time" | "Do not open" | "Open automatically";
export enum ShouldOpenDiagramEditorAutomaticallyConfiguration {
  ASK = "Ask next time",
  DO_NOT_OPEN = "Do not open",
  OPEN_AUTOMATICALLY = "Open automatically",
}

export class SwfVsCodeExtensionConfiguration {
  public shouldAutomaticallyOpenDiagramEditorAlongsideTextEditor(): ShouldOpenDiagramEditorAutomaticallyConfiguration {
    const configString = vscode.workspace
      .getConfiguration()
      .get(
        CONFIGURATION_SECTIONS.automaticallyOpenDiagramEditorAlongsideTextEditor,
        ShouldOpenDiagramEditorAutomaticallyConfiguration.ASK
      ) as ShouldOpenDiagramEditorAutomaticallyType;

    return this.stringToShouldOpenDiagramEditorAutomaticallyConfiguration(configString);
  }

  public stringToShouldOpenDiagramEditorAutomaticallyConfiguration(
    configString: ShouldOpenDiagramEditorAutomaticallyType
  ) {
    switch (configString) {
      case "Ask next time":
        return ShouldOpenDiagramEditorAutomaticallyConfiguration.ASK;
      case "Do not open":
        return ShouldOpenDiagramEditorAutomaticallyConfiguration.DO_NOT_OPEN;
      case "Open automatically":
        return ShouldOpenDiagramEditorAutomaticallyConfiguration.OPEN_AUTOMATICALLY;
      default:
        throw new Error("Unknown config " + configString);
    }
  }

  public async configureAutomaticallyOpenDiagramEditorAlongsideTextEditor() {
    const picked = await vscode.window.showQuickPick(
      [
        ShouldOpenDiagramEditorAutomaticallyConfiguration.OPEN_AUTOMATICALLY,
        ShouldOpenDiagramEditorAutomaticallyConfiguration.DO_NOT_OPEN,
      ],
      {
        canPickMany: false,
        title: "Open Serverless Workflow Diagram Editor automatically when opening Serverless Workflow files?",
      }
    );

    await vscode.workspace
      .getConfiguration()
      .update(CONFIGURATION_SECTIONS.automaticallyOpenDiagramEditorAlongsideTextEditor, picked);
  }

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
