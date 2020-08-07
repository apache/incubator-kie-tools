/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Capability, CapabilityResponse } from "@kogito-tooling/backend-api";
import { FileOperationEvent } from "./FileOperationEvent";

/**
 * Sample capability to watch for file changes inside a registered workspace.
 */
export interface WorkspaceWatcherCapability extends Capability {
  /**
   * Register a base path to be watched.
   * @param basePath Absolute path of the folder to be watched.
   */
  registerWorkspace(basePath: string): CapabilityResponse<void>;

  /**
   * Unregister the current registered workspace.
   */
  unregisterWorkspace(): CapabilityResponse<void>;

  /**
   * Fire an event when a file change occurs in the registered workspace.
   * @param event The event to be fired.
   */
  fireEvent(event: FileOperationEvent): CapabilityResponse<void>;
}
