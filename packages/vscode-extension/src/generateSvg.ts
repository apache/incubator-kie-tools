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
import * as fs from "fs";
import * as vscode from "vscode";
import { WorkspaceApi } from "@kogito-tooling/editor-envelope-protocol";

export async function generateSvg(editorStore1: KogitoEditorStore, workspaceApi: WorkspaceApi) {
    const editor = editorStore1.activeEditor;
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
    const svgFileName = `${parsedPath.name}${parsedPath.ext}.svg`;
    const svgAbsoluteFilePath = `${parsedPath.dir}/${svgFileName}`;
    fs.writeFileSync(svgAbsoluteFilePath, previewSvg);

    vscode.window.showInformationMessage(`SVG saved at '${svgFileName}'.`, "Open SVG").then(() => {
        workspaceApi.receive_openFile(svgAbsoluteFilePath);
    });
}