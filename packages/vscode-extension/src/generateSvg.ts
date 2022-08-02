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

import { VsCodeKieEditorStore } from "./VsCodeKieEditorStore";
import * as __path from "path";
import * as vscode from "vscode";
import { WorkspaceChannelApi } from "@kie-tools-core/workspace/dist/api";
import { VsCodeI18n } from "./i18n";
import { I18n } from "@kie-tools-core/i18n/dist/core";
import { EditorEnvelopeLocator } from "@kie-tools-core/editor/dist/api";
import {
  configurationTokenKeys,
  definitelyPosixPath,
  getInterpolatedConfigurationValue,
} from "./ConfigurationInterpolation";

const encoder = new TextEncoder();

export async function generateSvg(args: {
  editorStore: VsCodeKieEditorStore;
  workspaceApi: WorkspaceChannelApi;
  vsCodeI18n: I18n<VsCodeI18n>;
  displayNotification: boolean;
  editorEnvelopeLocator: EditorEnvelopeLocator;
}) {
  const i18n = args.vsCodeI18n.getCurrent();

  const editor =
    args.editorStore.activeEditor ??
    Array.from(args.editorStore.openEditors)
      .filter((e) => e.document.document.uri === vscode.window.activeTextEditor?.document.uri)
      .pop();

  if (!editor) {
    console.info(`Unable to create SVG because there's no matching Editor that can generate SVGs.`);
    return;
  }

  const previewSvg = await editor.getPreview();
  if (!previewSvg) {
    console.info(`Unable to create SVG for '${editor.document.document.uri.fsPath}'`);
    return;
  }

  const fileType = args.editorEnvelopeLocator.getEnvelopeMapping(
    __path.parse(editor.document.document.uri.path).base
  )?.type;
  const svgFilenameTemplateId = `kogito.${fileType}.svgFilenameTemplate`;
  const svgFilePathTemplateId = `kogito.${fileType}.svgFilePath`;

  const svgFilenameTemplate = vscode.workspace.getConfiguration().get(svgFilenameTemplateId, "");
  const svgFilePathTemplate = vscode.workspace.getConfiguration().get(svgFilePathTemplateId, "");

  if (__path.parse(svgFilenameTemplate).dir) {
    vscode.window.showErrorMessage(
      `The kogito.${fileType}.svgFilenameTemplate setting should be a valid filename, without a path prefix. Current value: ${svgFilenameTemplate}`
    );
    return;
  }

  const svgFileName = getInterpolatedConfigurationValue({
    currentFileAbsolutePosixPath: editor.document.document.uri.path,
    value:
      definitelyPosixPath(svgFilenameTemplate) || `${configurationTokenKeys["${fileBasenameNoExtension}"]}-svg.svg`,
  });
  const svgFilePath = getInterpolatedConfigurationValue({
    currentFileAbsolutePosixPath: editor.document.document.uri.path,
    value: definitelyPosixPath(svgFilePathTemplate) || `${configurationTokenKeys["${fileDirname}"]}`,
  });

  const svgUri = editor.document.document.uri.with({ path: __path.posix.resolve(svgFilePath, svgFileName) });

  await vscode.workspace.fs.writeFile(svgUri, encoder.encode(previewSvg));

  if (args.displayNotification) {
    vscode.window.showInformationMessage(i18n.savedSvg(svgFileName), i18n.openSvg).then((selection) => {
      if (selection !== i18n.openSvg) {
        return;
      }

      args.workspaceApi.kogitoWorkspace_openFile(svgUri.path);
    });
  }
}
