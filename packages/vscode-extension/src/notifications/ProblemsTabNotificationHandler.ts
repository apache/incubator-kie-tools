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

import { Notification, NotificationSeverity } from "@kie-tools-core/notifications/dist/api";
import * as vscode from "vscode";
import { getWorkspaceRoot } from "../workspace/workspaceRoot";
import { KogitoEditorDocument } from "../VsCodeKieEditorController";
import { toFsPath } from "../paths/paths";
import * as __path from "path";

const DIAGNOSTIC_COLLECTION_NAME = "kie-tools-vscode-extensions";

type NotificationSeverityConversionType = {
  [K in NotificationSeverity]: vscode.DiagnosticSeverity;
};

const KOGITO_NOTIFICATION_TO_VS_CODE_DIAGNOSTIC_SEVERITY_CONVERSION_MAP: NotificationSeverityConversionType = {
  INFO: vscode.DiagnosticSeverity.Information,
  WARNING: vscode.DiagnosticSeverity.Warning,
  ERROR: vscode.DiagnosticSeverity.Error,
  HINT: vscode.DiagnosticSeverity.Hint,
  SUCCESS: vscode.DiagnosticSeverity.Information,
};

export class ProblemsTabNotificationHandler {
  private readonly diagnosticCollection = vscode.languages.createDiagnosticCollection(DIAGNOSTIC_COLLECTION_NAME);

  public createNotification(document: KogitoEditorDocument["document"], notification: Notification): void {
    const workspaceRoot = getWorkspaceRoot(document);
    const uri = vscode.Uri.file(
      __path.join(
        workspaceRoot.workspaceRootAbsoluteFsPath,
        toFsPath(notification.normalizedPosixPathRelativeToTheWorkspaceRoot)
      )
    );
    const diagnostics: vscode.Diagnostic[] = this.diagnosticCollection.get(uri)?.map((elem) => elem) || [];
    diagnostics.push(this.buildDiagnostic(notification));
    this.diagnosticCollection.set(uri, diagnostics);
  }

  public setProblemsEntries(
    document: KogitoEditorDocument["document"],
    normalizedPosixPathRelativeToTheWorkspaceRoot: string,
    notifications: Notification[]
  ): void {
    const workspaceRoot = getWorkspaceRoot(document);
    const uri = vscode.Uri.file(
      __path.join(workspaceRoot.workspaceRootAbsoluteFsPath, toFsPath(normalizedPosixPathRelativeToTheWorkspaceRoot))
    );
    const diagnostics = notifications.map((notification) => this.buildDiagnostic(notification));
    this.diagnosticCollection.set(uri, diagnostics);
  }

  public removeNotifications(
    document: KogitoEditorDocument["document"],
    normalizedPosixPathRelativeToTheWorkspaceRoot: string
  ) {
    const workspaceRoot = getWorkspaceRoot(document);
    const uri = vscode.Uri.file(
      __path.join(workspaceRoot.workspaceRootAbsoluteFsPath, toFsPath(normalizedPosixPathRelativeToTheWorkspaceRoot))
    );
    this.diagnosticCollection.delete(uri);
  }

  private buildDiagnostic(notification: Notification): vscode.Diagnostic {
    const startLineNumber = notification.position?.startLineNumber ? notification.position?.startLineNumber - 1 : 0;
    const startColumn = notification.position?.startColumn ? notification.position?.startColumn - 1 : 0;
    const endColumn = notification.position?.endColumn || 0;
    const endLineNumber = notification.position?.endLineNumber ? notification.position?.endLineNumber - 1 : 0;
    return {
      message: notification.message,
      range: new vscode.Range(startLineNumber, startColumn, endLineNumber, endColumn),
      severity: this.getSeverity(notification.severity),
    };
  }

  private getSeverity(severity: NotificationSeverity): vscode.DiagnosticSeverity {
    const diagnostic = KOGITO_NOTIFICATION_TO_VS_CODE_DIAGNOSTIC_SEVERITY_CONVERSION_MAP[severity];
    return diagnostic ? vscode.DiagnosticSeverity.Information : diagnostic;
  }
}
