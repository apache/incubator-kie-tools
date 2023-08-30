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

import { HttpBridge, HttpService, LocalHttpServer, LocalHttpService } from "..";
import { Service } from "./Service";

/**
 * Service responsible for managing all backend services.
 */
export class BackendManagerService implements Service {
  private readonly serviceRegistry: Map<string, Service> = new Map();

  /**
   * @param args.bridge Service bridge. Required to start up HTTP services.
   * @param args.localHttpServer Local HTTP server. Required to start up local HTTP services.
   * @param args.bootstrapServices Services that should be started up along with the backend manager.
   * @param args.lazyServices Services that should be started upon the first usage.
   */
  public constructor(
    private readonly args: {
      bridge?: HttpBridge;
      localHttpServer?: LocalHttpServer;
      bootstrapServices?: Service[];
      lazyServices?: Service[];
    }
  ) {}

  public identify(): string {
    return "BACKEND_MANAGER";
  }

  public async start(): Promise<void> {
    if (this.args.localHttpServer) {
      await this.registerService(this.args.localHttpServer);
    }

    if (!this.args.bootstrapServices || this.args.bootstrapServices.length === 0) {
      return;
    }

    for (const service of this.args.bootstrapServices) {
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
        if (!this.args.bridge) {
          console.warn(`Could not register an HTTP service (${service.identify()}) without having an HTTP bridge.`);
          return false;
        }
        service.registerHttpBridge(this.args.bridge);
      }

      if (service instanceof LocalHttpService) {
        if (!this.args.localHttpServer || !this.serviceRegistry.get(this.args.localHttpServer.identify())) {
          console.warn(
            `Could not register a local HTTP service (${service.identify()}) without having a local server registered.`
          );
          return false;
        }
        service.registerPort(this.args.localHttpServer.getPort());
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

    if (!this.args.lazyServices || this.args.lazyServices.length === 0) {
      return;
    }

    const lazyService = this.args.lazyServices.find((s) => s.identify() === id);

    if (!lazyService || !(await this.registerService(lazyService))) {
      return;
    }

    return lazyService as T;
  }
}
