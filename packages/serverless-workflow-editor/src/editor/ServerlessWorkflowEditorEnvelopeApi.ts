import { Position } from "vscode-languageserver-types";
import { SwfServiceCatalogService } from "@kie-tools/serverless-workflow-service-catalog/dist/api";

export type SwfMonacoEditorCommandTypes =
  | "LogInToRhhcc"
  | "SetupServiceRegistryUrl"
  | "RefreshServiceCatalogFromRhhcc"
  | "ImportFunctionFromCompletionItem"
  | "OpenFunctionsWidget"
  | "OpenStatesWidget"
  | "OpenFunctionsCompletionItems";

export type SwfMonacoEditorCommandArgs = {
  LogInToRhhcc: {};
  SetupServiceRegistryUrl: {};
  RefreshServiceCatalogFromRhhcc: {};
  ImportFunctionFromCompletionItem: { containingService: SwfServiceCatalogService };
  OpenFunctionsWidget: { position: Position };
  OpenStatesWidget: { position: Position };
  OpenFunctionsCompletionItems: { newCursorPosition: Position };
};

export type SwfMonacoEditorCommandIds = Record<SwfMonacoEditorCommandTypes, string>;
