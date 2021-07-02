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

/**
 * A service that has an identity and lifecycle methods.
 */
export interface Service {
  /**
   * Inform the identity of the service.
   */
  identify(): string;

  /**
   * Start the service.
   */
  start(): Promise<void>;

  /**
   * Stop the service.
   */
  stop(): void;

  /**
   * Check whether all requirements are met or not before starting the service up.
   */
  satisfyRequirements(): Promise<boolean>;
}
