/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { EditorEnvelopeLocator } from "@kie-tools-core/editor/dist/api";
import { I18n } from "@kie-tools-core/i18n/dist/core";
import * as __path from "path";
import * as vscode from "vscode";
import {
  configurationTokenKeys,
  definitelyPosixPath,
  getInterpolatedConfigurationValue,
} from "./ConfigurationInterpolation";
import { VsCodeKieEditorStore } from "./VsCodeKieEditorStore";
import { VsCodeI18n } from "./i18n";
import { VsCodeWorkspaceChannelApiImpl } from "./workspace/VsCodeWorkspaceChannelApiImpl";

const encoder = new TextEncoder();

function kogitoCompatibleSvgDirname(fileType: string | undefined) {
  if (fileType === "bpmn" || fileType === "bpmn2") {
    return "processSVG";
  }

  if (fileType === "dmn") {
    return "decisionSVG";
  }

  return "anySVG";
}

export async function generateSvg(args: {
  editorStore: VsCodeKieEditorStore;
  vscodeWorkspace: VsCodeWorkspaceChannelApiImpl;
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

  const svgFilenameTemplate = vscode.workspace.getConfiguration().get(svgFilenameTemplateId, "" as string);
  const svgFilePathTemplate = vscode.workspace.getConfiguration().get(svgFilePathTemplateId, "" as string);

  if (__path.parse(svgFilenameTemplate).dir) {
    vscode.window.showErrorMessage(
      `The kogito.${fileType}.svgFilenameTemplate setting should be a valid filename, without a path prefix. Current value: ${svgFilenameTemplate}`
    );
    return;
  }

  const svgFileName = getInterpolatedConfigurationValue({
    currentFileAbsolutePosixPath: editor.document.document.uri.path,
    value: definitelyPosixPath(svgFilenameTemplate) || `${configurationTokenKeys["${fileBasenameNoExtension}"]}.svg`,
  });

  let svgFilePath;
  if (svgFilePathTemplate.trim() !== "" /* has customized setting for svgFilePath */) {
    svgFilePath = getInterpolatedConfigurationValue({
      currentFileAbsolutePosixPath: editor.document.document.uri.path,
      value: definitelyPosixPath(svgFilePathTemplate),
    });
  } else if (editor.document.document.uri.path.includes("/src/main/resources") /* is inside Kogito app */) {
    const split = editor.document.document.uri.path.split("/src/main/resources");
    svgFilePath = __path.posix.join(
      split[0],
      `src/main/resources/META-INF/${kogitoCompatibleSvgDirname(fileType)}`,
      __path.dirname(split[1])
    );
  } else {
    svgFilePath = getInterpolatedConfigurationValue({
      currentFileAbsolutePosixPath: editor.document.document.uri.path,
      value: `${configurationTokenKeys["${fileDirname}"]}`,
    });
  }

  const svgFilePathUri = editor.document.document.uri.with({ path: __path.posix.resolve(svgFilePath, svgFileName) });

  await vscode.workspace.fs.writeFile(svgFilePathUri, encoder.encode(previewSvg));

  if (args.displayNotification) {
    const svgFilePosixPathRelativeToOpenFile = __path.posix.relative(editor.document.document.uri.path, svgFilePath);
    vscode.window
      .showInformationMessage(i18n.savedSvg(svgFilePosixPathRelativeToOpenFile), i18n.openSvg)
      .then((selection) => {
        if (selection !== i18n.openSvg) {
          return;
        }

        args.vscodeWorkspace.openFile(svgFilePathUri.fsPath);
      });
  }
}
