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

interface MultipleResourcesPayload {
  mainURI: string;
  resources: ResourceWithURI[];
}

interface ResourceWithURI {
  URI: string;
  content: string;
}

function buildMultipleResourcesPayload(mainURI: string, documents: vscode.TextDocument[]): string {
  const body: MultipleResourcesPayload = {
    mainURI: mainURI,
    resources: documents.map((document) => ({
      URI: document.fileName,
      content: document.getText(),
    })),
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
  console.debug(`[Extended Services Extension] Fetching ${url.toString()}`);
  const response = await fetch(url.toString(), {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: buildMultipleResourcesPayload(textDocument.fileName, [textDocument]),
  });

  const responseData: any = await response.json();
  return parseFunction(responseData);
}

export async function validateBPMN(
  serviceURL: URL,
  bpmnFile: kiefile.KieFile
): Promise<validationresponse.BPMNValidationResponse[]> {
  try {
    return validate(serviceURL, bpmnFile, "/jitbpmn/validate", validationresponse.parseBPMNValidationResponse);
  } catch (error) {
    console.error(
      `[Extended Services Extension] An error happened while trying to validate ${bpmnFile} from serviceUrl ${serviceURL}: ${error.message}`
    );
    throw new Error("VALIDATE BPMN REQUEST ERROR: \n", error.message);
  }
}

export async function validateDMN(
  serviceURL: URL,
  dmnFile: kiefile.KieFile
): Promise<validationresponse.DMNValidationResponse[]> {
  try {
    return validate(serviceURL, dmnFile, "/jitdmn/validate", validationresponse.parseDMNValidationResponse);
  } catch (error) {
    console.error(
      `[Extended Services Extension] An error happened while trying to validate ${dmnFile} from serviceUrl ${serviceURL}: ${error.message}`
    );
    throw new Error("VALIDATE DMN REQUEST ERROR: \n", error.message);
  }
}
