/**
 * Copyright 2021 Red Hat, Inc. and/or its affiliates
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as vscode from "vscode";
import { Notification, NotificationsApi, NotificationSeverity } from "../api";

const DIAGNOSTIC_COLLECTION_NAME = "kogito";

type NotificationSeverityConvertionType = {
  [K in NotificationSeverity]: vscode.DiagnosticSeverity;
};

const KOGITO_NOTIFICATION_TO_VS_CODE_DIAGNOSTIC_SEVERITY_CONVERTION_MAP: NotificationSeverityConvertionType = {
  INFO: vscode.DiagnosticSeverity.Information,
  WARNING: vscode.DiagnosticSeverity.Warning,
  ERROR: vscode.DiagnosticSeverity.Error,
  HINT: vscode.DiagnosticSeverity.Hint,
  SUCCESS: vscode.DiagnosticSeverity.Information
};

export class ProblemsTabNotificationHandler implements NotificationsApi {
  private readonly diagnosticCollection = vscode.languages.createDiagnosticCollection(DIAGNOSTIC_COLLECTION_NAME);

  public createNotification(notification: Notification): void {
    const uri = vscode.Uri.file(notification.path);
    const diagnostics: vscode.Diagnostic[] = this.diagnosticCollection.get(uri)?.map(elem => elem) || [];
    diagnostics.push(this.buildDiagnostic(notification));
    this.diagnosticCollection.set(uri, diagnostics);
  }

  public setNotifications(path: string, notifications: Notification[]): void {
    const uri = vscode.Uri.file(path);
    const diagnostics = notifications.map(notification => this.buildDiagnostic(notification));
    this.diagnosticCollection.set(uri, diagnostics);
  }

  public removeNotifications(path: string) {
    this.diagnosticCollection.delete(vscode.Uri.file(path));
  }

  private buildDiagnostic(notification: Notification): vscode.Diagnostic {
    return {
      message: notification.message,
      range: new vscode.Range(new vscode.Position(0, 0), new vscode.Position(0, 0)),
      severity: this.getSeverity(notification.severity)
    };
  }

  private getSeverity(severity: NotificationSeverity): vscode.DiagnosticSeverity {
    const diagnostic = KOGITO_NOTIFICATION_TO_VS_CODE_DIAGNOSTIC_SEVERITY_CONVERTION_MAP[severity];
    return diagnostic ? vscode.DiagnosticSeverity.Information : diagnostic;
  }
}
