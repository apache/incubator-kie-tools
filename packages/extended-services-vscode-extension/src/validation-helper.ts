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
import { BPMNValidationResponse, DMNValidationResponse } from "./jit-executor/responses";
import { JITCommands } from "./jit-executor/jit-commands";
import { KIEFileWatcher } from "./watchers/kie-files/kie-file-watcher";

export class ValidationHelper {
  readonly source = "Apache KIE Extended Services";
  readonly clearValidationCommandUID: string = "extended-services-vscode-extension.clearValidation";
  readonly validateCommandUID: string = "extended-services-vscode-extension.validate";

  private context: vscode.ExtensionContext;
  private clearValidationCommand: vscode.Disposable;
  private validateCommand: vscode.Disposable;
  private diagnosticCollection;

  constructor(context: vscode.ExtensionContext) {
    this.context = context;
    this.diagnosticCollection = vscode.languages.createDiagnosticCollection("KIE Files Diagnostics");
    this.initializeCommand();
  }

  private initializeCommand(): void {
    const clearValidationCommandHandler = () => {
      this.clearValidation();
    };

    const validateCommandHandler = (serviceURL: URL) => {
      this.validate(serviceURL);
    };

    this.clearValidationCommand = vscode.commands.registerCommand(
      this.clearValidationCommandUID,
      clearValidationCommandHandler
    );
    this.validateCommand = vscode.commands.registerCommand(this.validateCommandUID, validateCommandHandler);

    this.context.subscriptions.push(this.validateCommand);
  }

  private createBPMNDiagnostics(document: vscode.TextDocument, validationResponses: BPMNValidationResponse[]): void {
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

    this.diagnosticCollection.set(document.uri, diagnostics);
  }

  private createDMNDiagnostics(document: vscode.TextDocument, validationResponses: DMNValidationResponse[]): void {
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

    this.diagnosticCollection.set(document.uri, diagnostics);
  }

  private async validateBPMN(serviceURL: URL, documents: vscode.TextDocument[]): Promise<void> {
    for (const document of documents) {
      try {
        const validationResponses: BPMNValidationResponse[] = await JITCommands.validateBPMN(serviceURL, [document]);
        this.createBPMNDiagnostics(document, validationResponses);
      } catch (error) {
        vscode.window.showErrorMessage("Validate BPMN error: " + error.message);
      }
    }
  }

  private async validateDMN(serviceURL: URL, documents: vscode.TextDocument[]): Promise<void> {
    for (const document of documents) {
      try {
        const validationResponses: DMNValidationResponse[] = await JITCommands.validateDMN(serviceURL, [document]);
        this.createDMNDiagnostics(document, validationResponses);
      } catch (error) {
        vscode.window.showErrorMessage("Validate DMN error: " + error.message);
      }
    }
  }

  private async validate(serviceURL: URL): Promise<void> {
    const bpmnFiles: vscode.TextDocument[] = await KIEFileWatcher.findOpenBPMNFiles();
    const dmnFiles: vscode.TextDocument[] = await KIEFileWatcher.findOpenDMNFiles();
    await new Promise((f) => setTimeout(f, 50));
    await this.validateBPMN(serviceURL, bpmnFiles);
    await this.validateDMN(serviceURL, dmnFiles);
  }

  private clearValidation(): void {
    this.diagnosticCollection.clear();
  }

  public dispose(): void {
    this.validateCommand.dispose();
    this.clearValidationCommand.dispose();
  }
}
