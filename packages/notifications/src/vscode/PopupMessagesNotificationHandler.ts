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

import { WorkspaceApi } from "@kie-tooling-core/workspace/dist/api";
import * as vscode from "vscode";
import { Notification, NotificationsApi, NotificationSeverity } from "../api";
import { I18n } from "@kie-tooling-core/i18n/dist/core";
import { NotificationsApiVsCodeI18nDictionary } from "./i18n";

export class PopupMessagesNotificationHandler implements NotificationsApi {
  constructor(
    private readonly workspaceApi: WorkspaceApi,
    private readonly i18n: I18n<NotificationsApiVsCodeI18nDictionary>
  ) {}

  public kogitoNotifications_createNotification(notification: Notification): void {
    this.getHandleStrategyForSeverity(notification.severity)(notification.message, notification.path);
  }

  public kogitoNotifications_setNotifications(path: string, notifications: Notification[]): void {
    if (notifications.length === 0) {
      return;
    }
    const errors = notifications.filter((n) => n.severity === "ERROR");
    const others = notifications.filter((n) => n.severity !== "ERROR");

    const errorsMessage = this.consolidateMessages(errors);
    const othersMessage = this.consolidateMessages(others);

    this.getHandleStrategyForSeverity("ERROR")(errorsMessage, path);
    this.getHandleStrategyForSeverity("SUCCESS")(othersMessage, path);
  }

  public kogitoNotifications_removeNotifications(path: string): void {
    // Popups can't be removed.
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
    return (message: string, path: string) =>
      path.length === 0
        ? showFunction(message)
        : showFunction(message, this.i18n.getCurrent().open).then((selected) => {
            if (!selected) {
              return;
            }
            this.workspaceApi.kogitoWorkspace_openFile(path);
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
