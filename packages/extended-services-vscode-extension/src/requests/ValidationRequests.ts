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
import * as validationresponse from "./ValidationResponse";
import * as kiefile from "../kieFiles/KieFile";

interface ValidationBody {
  mainURI: string;
  resources: ValidationResource[];
}

interface ValidationResource {
  URI: string;
  content: string;
}

function buildRequestBody(document: vscode.TextDocument): string {
  const body: ValidationBody = {
    mainURI: "VS Code KIE files",
    resources: [
      {
        URI: document.fileName,
        content: document.getText(),
      },
    ],
  };

  return JSON.stringify(body);
}

async function validate(
  serviceURL: URL,
  kieFile: kiefile.KieFile,
  endpoint: string,
  parseFunction: (data: any) => any[]
): Promise<any[]> {
  let textDocument = vscode.workspace.textDocuments.find((doc) => doc.uri === kieFile.uri);
  if (!textDocument) {
    textDocument = await vscode.workspace.openTextDocument(kieFile.uri);
  }
  const url = new URL(endpoint, serviceURL);
  const response = await fetch(url.toString(), {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: buildRequestBody(textDocument),
  });

  const responseData: any = await response.json();
  return parseFunction(responseData);
}

export async function validateBPMN(
  serviceURL: URL,
  kieFile: kiefile.KieFile
): Promise<validationresponse.BPMNValidationResponse[]> {
  try {
    return validate(serviceURL, kieFile, "/jitbpmn/validate", validationresponse.parseBPMNValidationResponse);
  } catch (error) {
    throw new Error("VALIDATE BPMN REQUEST ERROR: \n", error.message);
  }
}

export async function validateDMN(
  serviceURL: URL,
  kieFile: kiefile.KieFile
): Promise<validationresponse.DMNValidationResponse[]> {
  try {
    return validate(serviceURL, kieFile, "/jitdmn/validate", validationresponse.parseDMNValidationResponse);
  } catch (error) {
    throw new Error("VALIDATE DMN REQUEST ERROR: \n", error.message);
  }
}
