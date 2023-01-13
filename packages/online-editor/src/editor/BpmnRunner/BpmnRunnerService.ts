/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

export interface BpmnRunnerModelResource {
  URI: string;
  content: string;
}

export interface BpmnRunnerModelPayload {
  mainURI: string;
  resources: BpmnRunnerModelResource[];
  context?: any;
}

export class BpmnRunnerService {
  private readonly BPMN_RUNNER_VALIDATE_URL: string;

  constructor(private readonly jitExecutorUrl: string) {
    this.BPMN_RUNNER_VALIDATE_URL = `${this.jitExecutorUrl}jitbpmn/validate`;
  }

  public async validate(payload: BpmnRunnerModelPayload): Promise<[]> {
    if (!this.isPayloadValid(payload)) {
      return [];
    }

    const response = await fetch(this.BPMN_RUNNER_VALIDATE_URL, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(payload),
    });
    return await response.json();
  }

  private isPayloadValid(payload: BpmnRunnerModelPayload): boolean {
    return payload.resources.every((resource) => resource.content !== "");
  }
}
