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

import { I18n } from "@kie-tools-core/i18n/dist/core";
import { Notification, NotificationSeverity } from "@kie-tools-core/notifications/dist/api";
import * as vscode from "vscode";
import { VsCodeWorkspaceChannelApiImpl } from "../workspace/VsCodeWorkspaceChannelApiImpl";
import { NotificationsApiVsCodeI18nDictionary } from "./i18n";

export class PopupMessagesNotificationHandler {
  constructor(
    private readonly vscodeWorkspace: VsCodeWorkspaceChannelApiImpl,
    private readonly i18n: I18n<NotificationsApiVsCodeI18nDictionary>
  ) {}

  public createNotification(notification: Notification): void {
    this.getHandleStrategyForSeverity(notification.severity)(
      notification.message,
      notification.normalizedPosixPathRelativeToTheWorkspaceRoot
    );
  }

  public showAlert(normalizedPosixPathRelativeToTheWorkspaceRoot: string, notifications: Notification[]): void {
    if (notifications.length === 0) {
      return;
    }
    const errors = notifications.filter((n) => n.severity === "ERROR");
    const others = notifications.filter((n) => n.severity !== "ERROR");

    const errorsMessage = this.consolidateMessages(errors);
    const othersMessage = this.consolidateMessages(others);

    this.getHandleStrategyForSeverity("ERROR")(errorsMessage, normalizedPosixPathRelativeToTheWorkspaceRoot);
    this.getHandleStrategyForSeverity("SUCCESS")(othersMessage, normalizedPosixPathRelativeToTheWorkspaceRoot);
  }

  private getHandleStrategyForSeverity(severity: NotificationSeverity) {
    switch (severity) {
      case "ERROR":
        return this.handleStrategy(vscode.window.showErrorMessage);
      case "WARNING":
        return this.handleStrategy(vscode.window.showWarningMessage);
      default:
        return this.handleStrategy(vscode.window.showInformationMessage);
    }
  }

  private handleStrategy(showFunction: (message: string, ...items: string[]) => Thenable<string | undefined>) {
    return (message: string, normalizedPosixPathRelativeToTheWorkspaceRoot: string) =>
      normalizedPosixPathRelativeToTheWorkspaceRoot.length === 0
        ? showFunction(message)
        : showFunction(message, this.i18n.getCurrent().open).then((selected) => {
            if (!selected) {
              return;
            }
            this.vscodeWorkspace.openFile(normalizedPosixPathRelativeToTheWorkspaceRoot);
          });
  }

  private consolidateMessages(notifications: Notification[]): string {
    const messages = notifications.map((n) => n.message);
    if (messages.length > 0) {
      return messages.reduce((accum, current) => `${accum}\n${current}`);
    } else {
      return "";
    }
  }
}
