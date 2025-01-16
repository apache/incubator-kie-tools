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

import * as kieFile from "./kieFiles/KieFile";
import * as validationRequests from "./requests/ValidationRequests";
import * as validationResponse from "./requests/ValidationResponse";
import * as vscode from "vscode";

const source: string = "Apache KIE™ Extended Services";

function createBPMNDiagnostics(validationResponses: validationResponse.BPMNValidationResponse[]): vscode.Diagnostic[] {
  return validationResponses.map((validationResponse) => {
    const diagnostic = new vscode.Diagnostic(
      new vscode.Range(0, 0, 0, 0),
      validationResponse.error,
      vscode.DiagnosticSeverity.Error
    );
    diagnostic.source = source;
    return diagnostic;
  });
}

function createDMNDiagnostics(validationResponses: validationResponse.DMNValidationResponse[]): vscode.Diagnostic[] {
  return validationResponses.map((validationResponse) => {
    const diagnostic = new vscode.Diagnostic(
      new vscode.Range(0, 0, 0, 0),
      validationResponse.message,
      vscode.DiagnosticSeverity.Error
    );
    diagnostic.code = validationResponse.messageType;
    diagnostic.source = source;
    return diagnostic;
  });
}

export async function validateBPMN(serviceURL: URL, bpmnFile: kieFile.KieFile): Promise<vscode.Diagnostic[]> {
  try {
    const validationResponses: validationResponse.BPMNValidationResponse[] = await validationRequests.validateBPMN(
      serviceURL,
      bpmnFile
    );
    return createBPMNDiagnostics(validationResponses);
  } catch (error) {
    console.error(`An error occured while trying to validate BPMN file: ${bpmnFile} with error: ${error.message}`);
    throw new Error(`An error occured while trying to validate BPMN file: ${bpmnFile} with error: ${error.message}`);
  }
}

export async function validateDMN(serviceURL: URL, dmnFile: kieFile.KieFile): Promise<vscode.Diagnostic[]> {
  try {
    const validationResponses: validationResponse.DMNValidationResponse[] = await validationRequests.validateDMN(
      serviceURL,
      dmnFile
    );
    return createDMNDiagnostics(validationResponses);
  } catch (error) {
    console.error(`An error occured while trying to validate DMN file: ${dmnFile} with error: ${error.message}`);
    throw new Error(`An error occured while trying to validate DMN file: ${dmnFile} with error: ${error.message}`);
  }
}
