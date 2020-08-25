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

import { BackendProxy, Capability, CapabilityResponse } from "@kogito-tooling/backend-api";
import { BackendExtensionApi } from "@kogito-tooling/backend-channel-api";
import * as vscode from "vscode";

/**
 * Augmented {@link BackendProxy} for the VS Code channel.
 */
export class VsCodeBackendProxy extends BackendProxy {
  /**
   * @param backendExtensionId The backend extension ID in `publisher.name` format (optional).
   */
  public constructor(private readonly backendExtensionId?: string) {
    super();
  }

  public async withCapability<T extends Capability, U>(
    serviceId: string,
    consumer: (capability: T) => Promise<CapabilityResponse<U>>
  ): Promise<CapabilityResponse<U>> {
    await this.tryLoadBackendExtension();
    return super.withCapability(serviceId, consumer);
  }

  /**
   * Try to load the API from the backend extension if it hasn't been already loaded.
   * @returns True if the backend extension is loaded, otherwise false.
   */
  public async tryLoadBackendExtension(): Promise<boolean> {
    if (!this.backendExtensionId) {
      return false;
    }

    const backendExtension = vscode.extensions.getExtension(this.backendExtensionId);

    if (this.backendManager && backendExtension) {
      return true;
    }

    if (!backendExtension) {
      return false;
    }

    const backendExtensionApi: BackendExtensionApi = backendExtension.isActive
      ? backendExtension.exports
      : await backendExtension.activate();

    this.backendManager = backendExtensionApi.backendManager;
    return !!this.backendManager;
  }
}
