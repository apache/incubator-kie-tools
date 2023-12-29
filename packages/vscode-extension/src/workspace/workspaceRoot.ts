import * as vscode from "vscode";
import * as __path from "path";
import { KogitoEditorDocument } from "../VsCodeKieEditorController";

export function getNormalizedPosixPathRelativeToWorkspaceRoot(document: KogitoEditorDocument["document"]) {
  return vscode.workspace.workspaceFolders
    ? vscode.workspace.asRelativePath(document.uri, false)
    : __path.relative(__path.dirname(document.uri.path), document.uri.path);
}

export function getWorkspaceRoot(document: KogitoEditorDocument["document"]): {
  workspaceRootAbsoluteFsPath: string;
  type: "dangling" | "workspace";
} {
  const workspaceFolder = vscode.workspace.workspaceFolders?.find((wf) =>
    document.uri.fsPath.startsWith(wf.uri.fsPath)
  );

  if (workspaceFolder) {
    return {
      type: "workspace",
      workspaceRootAbsoluteFsPath: workspaceFolder.uri.fsPath,
    };
  } else {
    return {
      type: "dangling",
      workspaceRootAbsoluteFsPath: __path.dirname(document.uri.path),
    };
  }
}
