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

export interface JitDmnPayload {
  model: string;
  context: Map<string, object>;
}

const JIT_DMN_URL = "http://localhost:8080/jitdmn";
const JIT_DMN_SCHEMA_URL = "http://localhost:8080/jitdmn/schema";
const JIT_DOWNLOAD = "https://kiegroup.github.io/kogito-online-ci/temp/runner.zip";

export class JitDmn {
  public static async download() {
    try {
      const response = await fetch(JIT_DOWNLOAD, { method: "GET" });
      const blob = await response.blob();

      const objectUrl = URL.createObjectURL(blob);
      window.open(objectUrl, "_blank");
      URL.revokeObjectURL(objectUrl);
    } catch (err) {
      console.error("Automatic JIT download failed.");
    }
  }

  public static validateForm(payload: JitDmnPayload) {
    return fetch(JIT_DMN_URL, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Accept: "application/json, text/plain, */*"
      },
      body: JSON.stringify(payload)
    });
  }

  public static async getFormSchema(model: string) {
    const response = await fetch(JIT_DMN_SCHEMA_URL, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Accept: "application/json, text/plain, */*"
      },
      body: model
    });
    return await response.json();
  }
}
