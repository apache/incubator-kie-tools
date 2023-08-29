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

import { CapabilityResponseStatus } from "./CapabilityResponseStatus";

/**
 * Arguments to build a capability response.
 */
interface CapabilityResponseArgs<T> {
  status: CapabilityResponseStatus;
  body?: T;
  message?: string;
}

/**
 * Wrapper object for capability responses.
 */
export class CapabilityResponse<T> {
  public readonly status: CapabilityResponseStatus;
  public readonly body?: T;
  public readonly message?: string;
  public constructor(args: CapabilityResponseArgs<T>) {
    this.status = args.status;
    this.body = args.body;
    this.message = args.message;
  }

  /**
   * Utility to create a response with OK status and optional body.
   * @param body Optional body that the response can include.
   * @returns A capability response with OK status.
   */
  public static ok<U>(body?: U): CapabilityResponse<U> {
    return new CapabilityResponse({ status: CapabilityResponseStatus.OK, body: body });
  }

  /**
   * Utility to create a response with NOT_AVAILABLE status and message.
   * @param message Message associated with the unavailability of the capability.
   * @returns A capability response with NOT_AVAILABLE status.
   */
  public static notAvailable<U>(message: string): CapabilityResponse<U> {
    return new CapabilityResponse({ status: CapabilityResponseStatus.NOT_AVAILABLE, message: message });
  }

  /**
   * Utility to create a response with MISSING_INFRA status.
   * @returns A capability response with MISSING_INFRA status.
   */
  public static missingInfra<U>(): CapabilityResponse<U> {
    return new CapabilityResponse({ status: CapabilityResponseStatus.MISSING_INFRA });
  }
}
