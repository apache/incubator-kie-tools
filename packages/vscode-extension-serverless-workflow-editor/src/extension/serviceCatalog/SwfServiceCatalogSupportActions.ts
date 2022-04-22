import {
  SwfServiceCatalogService,
  SwfServiceCatalogServiceSourceType,
} from "@kie-tools/serverless-workflow-service-catalog/src/api";
import * as vscode from "vscode";
import { getServiceFileNameFromSwfServiceCatalogServiceId } from "./rhhccServiceRegistry";
import { posix as posixPath } from "path";
import { SwfVsCodeExtensionConfiguration } from "../configuration";
import { SwfServiceCatalogStore } from "./SwfServiceCatalogStore";

const encoder = new TextEncoder();

export class SwfServiceCatalogSupportActions {
  constructor(
    private readonly args: {
      configuration: SwfVsCodeExtensionConfiguration;
      swfServiceCatalogGlobalStore: SwfServiceCatalogStore;
    }
  ) {}

  public refresh(): void {
    vscode.window.setStatusBarMessage("Serverless Workflow Editor: Refreshing...");
    this.args.swfServiceCatalogGlobalStore.refresh().then(() => vscode.window.setStatusBarMessage(""));
  }

  public importFunctionFromCompletionItem(args: {
    containingService: SwfServiceCatalogService;
    documentUri: string;
  }): void {
    if (args.containingService.source.type === SwfServiceCatalogServiceSourceType.LOCAL_FS) {
      return;
    }

    const serviceFileName = getServiceFileNameFromSwfServiceCatalogServiceId(args.containingService.source.id);
    const specsDirAbsolutePosixPath = this.args.configuration.getInterpolatedSpecsDirAbsolutePosixPath({
      baseFileAbsolutePosixPath: vscode.Uri.parse(args.documentUri).path,
    });

    const serviceFileAbsolutePosixPath = posixPath.join(specsDirAbsolutePosixPath, serviceFileName);
    vscode.workspace.fs.writeFile(
      vscode.Uri.parse(serviceFileAbsolutePosixPath),
      encoder.encode(args.containingService.rawContent)
    );
    vscode.window.showInformationMessage(`Wrote ${serviceFileAbsolutePosixPath}.`);
  }
}
