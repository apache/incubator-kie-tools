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
import * as kieFilesFetcher from "./kieFiles/KieFilesFetcher";
import * as validationRequests from "./requests/ValidationRequests";
import * as validationResponse from "./requests/ValidationResponse";
import * as vscode from "vscode";

export class Validator {
  readonly source = "Apache KIE Extended Services";
  readonly clearValidationCommandUID: string = "extended-services-vscode-extension.clearValidation";
  readonly validateCommandUID: string = "extended-services-vscode-extension.validate";

  private clearValidationCommand: vscode.Disposable;
  private validateCommand: vscode.Disposable;
  private diagnosticCollection: vscode.DiagnosticCollection;

  private readonly clearValidationCommandHandler = () => {
    this.clearValidation();
  };
  private readonly validateCommandHandler = (serviceURL: URL) => {
    this.validate(serviceURL);
  };

  constructor() {
    this.diagnosticCollection = vscode.languages.createDiagnosticCollection("KIE Files Diagnostics");
    this.clearValidationCommand = vscode.commands.registerCommand(
      this.clearValidationCommandUID,
      this.clearValidationCommandHandler
    );
    this.validateCommand = vscode.commands.registerCommand(this.validateCommandUID, this.validateCommandHandler);
  }

  private clearValidation(): void {
    this.diagnosticCollection.clear();
  }

  private createBPMNDiagnostics(
    kieFile: kieFile.KieFile,
    validationResponses: validationResponse.BPMNValidationResponse[]
  ): void {
    const diagnostics: vscode.Diagnostic[] = [];

    for (const validationResponse of validationResponses) {
      const diagnostic = new vscode.Diagnostic(
        new vscode.Range(0, 0, 0, 0),
        validationResponse.error,
        vscode.DiagnosticSeverity.Error
      );
      diagnostic.source = this.source;
      diagnostics.push(diagnostic);
    }

    this.diagnosticCollection.set(kieFile.uri, diagnostics);
  }

  private createDMNDiagnostics(
    kieFile: kieFile.KieFile,
    validationResponses: validationResponse.DMNValidationResponse[]
  ): void {
    const diagnostics: vscode.Diagnostic[] = [];

    for (const validationResponse of validationResponses) {
      const diagnostic = new vscode.Diagnostic(
        new vscode.Range(0, 0, 0, 0),
        validationResponse.message,
        vscode.DiagnosticSeverity.Error
      );
      diagnostic.code = validationResponse.messageType;
      diagnostic.source = this.source;

      diagnostics.push(diagnostic);
    }

    this.diagnosticCollection.set(kieFile.uri, diagnostics);
  }

  public async validateBPMN(serviceURL: URL, kieFiles: kieFile.KieFile[]): Promise<void> {
    for (const kieFile of kieFiles) {
      try {
        const validationResponses: validationResponse.BPMNValidationResponse[] = await validationRequests.validateBPMN(
          serviceURL,
          kieFile
        );
        this.createBPMNDiagnostics(kieFile, validationResponses);
      } catch (error) {
        vscode.window.showErrorMessage("Validate BPMN error: " + error.message);
      }
    }
  }

  public async validateDMN(serviceURL: URL, kieFiles: kieFile.KieFile[]): Promise<void> {
    for (const kieFile of kieFiles) {
      try {
        const validationResponses: validationResponse.DMNValidationResponse[] = await validationRequests.validateDMN(
          serviceURL,
          kieFile
        );
        this.createDMNDiagnostics(kieFile, validationResponses);
      } catch (error) {
        vscode.window.showErrorMessage("Validate DMN error: " + error.message);
      }
    }
  }

  public async validate(serviceURL: URL): Promise<void> {
    const bpmnFiles: kieFile.KieFile[] = await kieFilesFetcher.findActiveKieFiles([kieFilesFetcher.bpmnDocumentFilter]);
    const dmnFiles: kieFile.KieFile[] = await kieFilesFetcher.findActiveKieFiles([kieFilesFetcher.dmnDocumentFilter]);
    await this.validateBPMN(serviceURL, bpmnFiles);
    await this.validateDMN(serviceURL, dmnFiles);
  }

  public dispose(): void {
    this.validateCommand.dispose();
    this.clearValidationCommand.dispose();
  }
}
