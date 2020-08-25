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

import { I18n } from "@kogito-tooling/i18n/dist/core";
import * as vscode from "vscode";
import { VsCodeI18n } from "./i18n";

const SUGGEST_BACKEND_KEY = "SUGGEST_BACKEND";

/**
 * If `SUGGEST_BACKEND` config is enabled,
 * show a notification informing the user to install the backend extension.
 * @param vsCodeI18n I18n for VS Code.
 * @param context The `vscode.ExtensionContext` provided on the activate method of the extension.
 * @param backendExtensionId The backend extension ID in `publisher.name` format.
 */
export function maybeSuggestBackendExtension(
  vsCodeI18n: I18n<VsCodeI18n>,
  context: vscode.ExtensionContext,
  backendExtensionId: string
): void {
  const suggestBackend = context.globalState.get<boolean>(SUGGEST_BACKEND_KEY) ?? true;

  if (!suggestBackend) {
    return;
  }

  const i18n = vsCodeI18n.getCurrent();

  vscode.window
    .showInformationMessage(i18n.installBackendExtensionMessage, i18n.installExtension, i18n.dontShowAgain)
    .then(async selection => {
      if (!selection) {
        return;
      }

      if (selection === i18n.installExtension) {
        await vscode.env.openExternal(vscode.Uri.parse(`${vscode.env.uriScheme}:extension/${backendExtensionId}`));
      }

      if (selection === i18n.dontShowAgain) {
        context.globalState.update(SUGGEST_BACKEND_KEY, false);
      }
    });
}
