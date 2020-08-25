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

import * as vscode from "vscode";

const SUGGEST_BACKEND_KEY = "SUGGEST_BACKEND";

/**
 * If `SUGGEST_BACKEND` config is enabled,
 * show a notification informing the user to install the backend extension.
 * @param context The `vscode.ExtensionContext` provided on the activate method of the extension.
 * @param backendExtensionId The backend extension ID in `publisher.name` format.
 */
export function maybeSuggestBackendExtension(context: vscode.ExtensionContext, backendExtensionId: string): void {
  const suggestBackend = context.globalState.get<boolean>(SUGGEST_BACKEND_KEY) ?? true;

  if (!suggestBackend) {
    return;
  }

  const message = "Consider installing the backend extension to augment the capabilities of the editors.";
  const installOption = "Install extension";
  const dontShowAgainOption = "Don't show again";

  vscode.window.showInformationMessage(message, installOption, dontShowAgainOption).then(async selection => {
    if (!selection) {
      return;
    }

    if (selection === installOption) {
      await vscode.env.openExternal(vscode.Uri.parse(`${vscode.env.uriScheme}:extension/${backendExtensionId}`));
    }

    if (selection === dontShowAgainOption) {
      context.globalState.update(SUGGEST_BACKEND_KEY, false);
    }
  });
}
