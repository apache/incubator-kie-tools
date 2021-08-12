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

export class KieToolingExtendedServicesBridge {
  private readonly KIE_TOOLING_EXTENDED_SERVICES_SERVER_URL: string;
  private readonly KIE_TOOLING_EXTENDED_SERVICES_PING: string;

  public constructor(private readonly port: string) {
    this.KIE_TOOLING_EXTENDED_SERVICES_SERVER_URL = `http://localhost:${port}`;
    this.KIE_TOOLING_EXTENDED_SERVICES_PING = `${this.KIE_TOOLING_EXTENDED_SERVICES_SERVER_URL}/ping`;
  }

  public async check(): Promise<boolean> {
    const response = await fetch(this.KIE_TOOLING_EXTENDED_SERVICES_SERVER_URL, { method: "OPTIONS" });
    return response.status < 300;
  }

  public async version(): Promise<string> {
    const response = await fetch(this.KIE_TOOLING_EXTENDED_SERVICES_PING, {
      method: "GET",
    });
    const json = await response.json();
    return json.App.Version;
  }
}
