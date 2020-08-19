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

import { Service } from "./Service";
import { HttpBridge, HttpService, LocalHttpServer, LocalHttpService } from "..";

/**
 * Service responsible for managing all backend services.
 */
export class BackendManagerService implements Service {
  private readonly serviceRegistry: Map<string, Service> = new Map();

  /**
   * @param bridge Service bridge. Required to start up HTTP services.
   * @param localHttpServer Local HTTP server. Required to start up local HTTP services.
   * @param bootstrapServices Services that should be started up along with the backend manager.
   * @param lazyServices Services that should be started upon the first usage.
   */
  public constructor(
    private readonly bridge?: HttpBridge,
    private readonly localHttpServer?: LocalHttpServer,
    private readonly bootstrapServices?: Service[],
    private readonly lazyServices?: Service[]
  ) {}

  public identify(): string {
    return "BACKEND_MANAGER";
  }

  public async start(): Promise<void> {
    if (this.localHttpServer) {
      await this.registerService(this.localHttpServer);
    }

    if (!this.bootstrapServices || this.bootstrapServices.length === 0) {
      return;
    }

    for (const service of this.bootstrapServices) {
      await this.registerService(service);
    }
  }

  public stop(): void {
    this.serviceRegistry.forEach((service) => service.stop());
    this.serviceRegistry.clear();
  }

  public async satisfyRequirements(): Promise<boolean> {
    return true;
  }

  /**
   * Register and start up a new service.
   * @param service Service to be registered.
   * @returns True if registration succeeded otherwise false.
   */
  public async registerService(service: Service): Promise<boolean> {
    if (this.serviceRegistry.has(service.identify())) {
      return true;
    }

    if (!(await service.satisfyRequirements())) {
      console.warn(`Could not satisfy requirements for service ${service.identify()}. Skipping registration.`);
      return false;
    }

    try {
      await service.start();

      if (service instanceof HttpService) {
        if (!this.bridge) {
          console.warn(`Could not register an HTTP service (${service.identify()}) without having an HTTP bridge.`);
          return false;
        }
        service.registerHttpBridge(this.bridge);
      }

      if (service instanceof LocalHttpService) {
        if (!this.localHttpServer || !this.serviceRegistry.get(this.localHttpServer.identify())) {
          console.warn(
            `Could not register a local HTTP service (${service.identify()}) without having a local server registered.`
          );
          return false;
        }
        service.registerPort(this.localHttpServer.getPort());
      }

      this.serviceRegistry.set(service.identify(), service);
      return true;
    } catch (e) {
      console.error(`An error has occurred while starting ${service.identify()} up: ${e}`);
      return false;
    }
  }

  /**
   * Look up for a service from the registry by its identifier.
   * @param id The identifier of the service.
   * @returns The required service if it is registered otherwise undefined.
   */
  public async getService<T extends Service>(id: string): Promise<T | undefined> {
    const registeredService = this.serviceRegistry.get(id);

    if (registeredService) {
      return registeredService as T;
    }

    if (!this.lazyServices || this.lazyServices.length === 0) {
      return;
    }

    const lazyService = this.lazyServices.find((s) => s.identify() === id);

    if (!lazyService || !(await this.registerService(lazyService))) {
      return;
    }

    return lazyService as T;
  }
}
