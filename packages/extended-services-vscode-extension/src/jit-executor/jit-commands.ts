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

import * as vscode from "vscode";
import {
  BPMNValidationResponse,
  BPMNValidationResponseParser,
  DMNValidationResponse,
  DMNValidationResponseParser,
  PingResponse,
} from "./responses";
import { KIEValidateBodyPayload, KIEValidateResourcePayload } from "./payloads";

export class JITCommands {
  public static async ping(serviceURL: URL): Promise<PingResponse> {
    const url = new URL("/ping", serviceURL);

    try {
      const response = await fetch(url.toString());
      const responseData = (await response.json()) as PingResponse;
      return responseData;
    } catch (error) {
      vscode.window.showErrorMessage("Error at JIT-Commands Ping request: ", error.message);
      return {
        started: false,
        version: "undefined",
      };
    }
  }

  public static async validateBPMN(
    serviceURL: URL,
    documents: vscode.TextDocument[]
  ): Promise<BPMNValidationResponse[]> {
    const url = new URL("/jitbpmn/validate", serviceURL);

    try {
      const response = await fetch(url.toString(), {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JITCommands.buildBodyPayload(documents),
      });

      const responseData: string[] = (await response.json()) as string[];
      const validationResponses: BPMNValidationResponse[] = BPMNValidationResponseParser.parse(responseData);
      return validationResponses;
    } catch (error) {
      vscode.window.showErrorMessage("Error at JIT-Commands Validate BPMN request: ", error.message);
      throw error;
    }
  }

  public static async validateDMN(serviceURL: URL, documents: vscode.TextDocument[]): Promise<DMNValidationResponse[]> {
    const url = new URL("/jitdmn/validate", serviceURL);

    try {
      const response = await fetch(url.toString(), {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JITCommands.buildBodyPayload(documents),
      });

      const responseData: any = await response.json();
      const validationResponses: DMNValidationResponse[] = DMNValidationResponseParser.parse(responseData);
      return validationResponses;
    } catch (error) {
      vscode.window.showErrorMessage("Error at JIT-Commands Validate DMN request: ", error.message);
      throw error;
    }
  }

  private static buildBodyPayload(documents: vscode.TextDocument[]): string {
    const bodyPayload: KIEValidateBodyPayload = {
      mainURI: "VS Code KIE files",
      resources: [],
    };

    for (const document of documents) {
      const resourcePayload: KIEValidateResourcePayload = {
        content: document.getText(),
        URI: document.fileName,
      };
      bodyPayload.resources.push(resourcePayload);
    }

    try {
      return JSON.stringify(bodyPayload);
    } catch (error) {
      vscode.window.showErrorMessage("Error building body payload: ", error.message);
      return "";
    }
  }
}
