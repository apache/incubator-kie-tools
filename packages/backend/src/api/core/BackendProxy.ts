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

import { BackendManagerService } from "./BackendManagerService";
import { Capability } from "./Capability";
import { CapabilityResponse } from "./CapabilityResponse";

/**
 * Expose all relevant methods for clients accessing backend services.
 */
export class BackendProxy {
  protected backendManager: BackendManagerService | undefined;

  /**
   * Register the backend manager.
   * @param backendManager The backend manager
   */
  public registerBackendManager(backendManager: BackendManagerService) {
    this.backendManager = backendManager;
  }

  /**
   * Execute the given callback if the capability is resolved otherwise reject the promise.
   * @param serviceId Id of the service associated with the capability.
   * @param consumer Consumer to run.
   */
  public async withCapability<T extends Capability, U>(
    serviceId: string,
    consumer: (capability: T) => Promise<CapabilityResponse<U>>
  ): Promise<CapabilityResponse<U>> {
    if (!this.backendManager) {
      return CapabilityResponse.missingInfra();
    }

    const service = await this.backendManager.getService(serviceId);

    if (!service) {
      return CapabilityResponse.notAvailable(`Service ${serviceId} not available.`);
    }

    return consumer(service as Capability as T);
  }

  /**
   * Stop all backend services through backend manager.
   */
  public stopServices(): void {
    if (!this.backendManager) {
      return;
    }
    this.backendManager.stop();
  }
}
