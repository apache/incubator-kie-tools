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

import { Service } from "..";

/**
 * A service that controls a local HTTP server.
 */
export abstract class LocalHttpServer implements Service {
  /**
   * Port number of the local http server.
   */
  protected port: number;

  /**
   * Inform the registered port.
   */
  public getPort(): number {
    return this.port;
  }

  public abstract identify(): string;

  public abstract start(): Promise<void>;

  public abstract stop(): void;

  public abstract satisfyRequirements(): Promise<boolean>;
}
