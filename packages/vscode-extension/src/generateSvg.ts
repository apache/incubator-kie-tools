/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { KogitoEditorStore } from "./KogitoEditorStore";
import * as __path from "path";
import * as vscode from "vscode";
import { WorkspaceApi } from "@kie-tools-core/workspace/dist/api";
import { VsCodeI18n } from "./i18n";
import { I18n } from "@kie-tools-core/i18n/dist/core";

const encoder = new TextEncoder();

type SettingsValueInterpolationToken =
  | "${workspaceFolder}"
  | "${fileDirname}"
  | "${fileExtname}"
  | "${fileBasename}"
  | "${fileBasenameNoExtension}";

export function interpolateSettingsValue(args: { tokens: Record<string, string>; value: string }) {
  const { tokens, value } = args;
  return Object.entries(tokens).reduce(
    (result, [tokenName, tokenValue]) => result.replaceAll(tokenName, tokenValue),
    value
  );
}

export async function generateSvg(args: {
  editorStore: KogitoEditorStore;
  workspaceApi: WorkspaceApi;
  vsCodeI18n: I18n<VsCodeI18n>;
  displayNotification: boolean;
}) {
  const i18n = args.vsCodeI18n.getCurrent();

  const editor = args.editorStore.activeEditor;
  if (!editor) {
    console.info(`Unable to create SVG because there's no Editor open.`);
    return;
  }

  const previewSvg = await editor.getPreview();
  if (!previewSvg) {
    console.info(`Unable to create SVG for '${editor.document.uri.fsPath}'`);
    return;
  }

  const parsedPath = __path.parse(editor.document.uri.fsPath);
  const workspace = vscode.workspace.workspaceFolders?.length
    ? vscode.workspace.workspaceFolders.find((workspace) => {
        const relative = __path.relative(workspace.uri.fsPath, editor.document.uri.fsPath);
        return relative && !relative.startsWith("..") && !__path.isAbsolute(relative);
      })
    : undefined;

  const tokens: Record<SettingsValueInterpolationToken, string> = {
    "${workspaceFolder}": workspace?.uri.fsPath ?? parsedPath.dir,
    "${fileDirname}": parsedPath.dir,
    "${fileExtname}": parsedPath.ext,
    "${fileBasename}": parsedPath.base,
    "${fileBasenameNoExtension}": parsedPath.name,
  };

  const fileExtensionNoDot = parsedPath.ext.replace(".", "");

  const svgFilenameTemplateId = `kogito.${fileExtensionNoDot}.svgFilenameTemplate`;
  const svgFilePathTemplateId = `kogito.${fileExtensionNoDot}.svgFilePath`;

  const svgFilenameTemplate = vscode.workspace.getConfiguration().get(svgFilenameTemplateId, "");
  const svgFilePathTemplate = vscode.workspace.getConfiguration().get(svgFilePathTemplateId, "");

  if (__path.parse(svgFilenameTemplate).dir) {
    vscode.window.showErrorMessage(
      `The kogito.${fileExtensionNoDot}.svgFilenameTemplate setting should be a valid filename, without a path prefix. Current value: ${svgFilenameTemplate}`
    );
    return;
  }

  const svgFileName = interpolateSettingsValue({
    tokens,
    value: svgFilenameTemplate ? svgFilenameTemplate : "${fileBasenameNoExtension}-svg.svg",
  });
  const svgFilePath = interpolateSettingsValue({
    tokens,
    value: svgFilePathTemplate ? svgFilePathTemplate : "${fileDirname}",
  });
  const svgUri = editor.document.uri.with({ path: __path.resolve(svgFilePath, svgFileName) });

  await vscode.workspace.fs.writeFile(svgUri, encoder.encode(previewSvg));

  if (args.displayNotification) {
    vscode.window.showInformationMessage(i18n.savedSvg(svgFileName), i18n.openSvg).then((selection) => {
      if (selection !== i18n.openSvg) {
        return;
      }

      args.workspaceApi.kogitoWorkspace_openFile(svgUri.fsPath);
    });
  }
}
