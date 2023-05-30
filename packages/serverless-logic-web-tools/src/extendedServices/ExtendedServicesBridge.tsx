/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

interface ExtendedServicesPingResponse {
  version: string;
}

export class ExtendedServicesBridge {
  private readonly KIE_SANDBOX_EXTENDED_SERVICES_PING: string;

  public constructor(private readonly baseUrl: string) {
    this.KIE_SANDBOX_EXTENDED_SERVICES_PING = `${this.baseUrl}/ping`;
  }

  public async check(): Promise<boolean> {
    const response = await fetch(this.baseUrl, { method: "OPTIONS" });
    return response.status < 300;
  }

  public async version(): Promise<ExtendedServicesPingResponse> {
    const response = await fetch(this.KIE_SANDBOX_EXTENDED_SERVICES_PING, {
      method: "GET",
    });
    return await response.json();
  }
}
