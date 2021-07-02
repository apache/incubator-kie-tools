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

import { I18n } from "@kie-tooling-core/i18n/dist/core/I18n";
import * as vscode from "vscode";
import { BackendProxy, Capability, CapabilityResponse, CapabilityResponseStatus } from "../api";
import { BackendExtensionApi } from "../channel-api";
import { BackendI18n } from "../i18n";

const SUGGEST_BACKEND_KEY = "SUGGEST_BACKEND";

/**
 * Augmented {@link BackendProxy} for the VS Code channel.
 */
export class VsCodeBackendProxy extends BackendProxy {
  /**
   * @param context The `vscode.ExtensionContext` provided on the activate method of the extension.
   * @param backendI18n I18n for backend services.
   * @param backendExtensionId The backend extension ID in `publisher.name` format (optional).
   */
  public constructor(
    private readonly context: vscode.ExtensionContext,
    private readonly backendI18n: I18n<BackendI18n>,
    private readonly backendExtensionId?: string
  ) {
    super();
  }

  public async withCapability<T extends Capability, U>(
    serviceId: string,
    consumer: (capability: T) => Promise<CapabilityResponse<U>>
  ): Promise<CapabilityResponse<U>> {
    await this.tryLoadBackendExtension(false);

    const response = await super.withCapability(serviceId, consumer);
    if (response.status === CapabilityResponseStatus.MISSING_INFRA) {
      this.suggestBackendExtension();
    }
    return response;
  }

  /**
   * Try to load the API from the backend extension if it hasn't been already loaded.
   * @param suggestInstall Whether or not to suggest installing the backend extension in case it is missing.
   */
  public async tryLoadBackendExtension(suggestInstall: boolean): Promise<void> {
    if (!this.backendExtensionId) {
      return; // Ignoring since no backend extension ID is provided
    }

    const backendExtension = vscode.extensions.getExtension(this.backendExtensionId);

    if (!backendExtension) {
      if (suggestInstall) {
        this.trySuggestBackendExtension();
      }
      return; // Backend extension is not installed
    }

    if (this.backendManager && backendExtension) {
      return; // Backend extension API already loaded
    }

    const backendExtensionApi: BackendExtensionApi = backendExtension.isActive
      ? backendExtension.exports
      : await backendExtension.activate();

    this.backendManager = backendExtensionApi.backendManager;
  }

  /**
   * If `SUGGEST_BACKEND` config is enabled,
   * show a notification informing the user to install the backend extension.
   * This notification also includes a `Don't show again` button to disable the `SUGGEST_BACKEND` config.
   */
  public trySuggestBackendExtension(): void {
    const suggestBackend = this.context.globalState.get<boolean>(SUGGEST_BACKEND_KEY) ?? true;

    if (!suggestBackend) {
      return;
    }

    const i18n = this.backendI18n.getCurrent();

    vscode.window
      .showInformationMessage(i18n.installBackendExtensionMessage, i18n.installExtension, i18n.dontShowAgain)
      .then(async (selection) => {
        if (!selection) {
          return;
        }

        if (selection === i18n.installExtension) {
          await vscode.env.openExternal(
            vscode.Uri.parse(`${vscode.env.uriScheme}:extension/${this.backendExtensionId}`)
          );
        }

        if (selection === i18n.dontShowAgain) {
          this.context.globalState.update(SUGGEST_BACKEND_KEY, false);
        }
      });
  }

  /**
   * Show a notification informing the user to install the backend extension.
   */
  public suggestBackendExtension(): void {
    const i18n = this.backendI18n.getCurrent();

    vscode.window
      .showInformationMessage(i18n.installBackendExtensionMessage, i18n.installExtension)
      .then(async (selection) => {
        if (!selection) {
          return;
        }

        if (selection === i18n.installExtension) {
          await vscode.env.openExternal(
            vscode.Uri.parse(`${vscode.env.uriScheme}:extension/${this.backendExtensionId}`)
          );
        }
      });
  }
}
