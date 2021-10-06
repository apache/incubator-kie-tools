/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { Notification, NotificationsApi, NotificationType } from "../api";
import { WorkspaceApi } from "@kie-tooling-core/workspace/dist/api";
import { PopupMessagesNotificationHandler } from "./PopupMessagesNotificationHandler";
import { ProblemsTabNotificationHandler } from "./ProblemsTabNotificationHandler";
import { I18n } from "@kie-tooling-core/i18n/dist/core";
import * as vscode from "vscode";
import { notificationsApiVsCodeI18nDefaults, notificationsApiVsCodeI18nDictionaries } from "./i18n";

type NotificationsApiHandlersMap = {
  [K in NotificationType]: NotificationsApi;
};

export class VsCodeNotificationsApi implements NotificationsApi {
  private readonly strategies: NotificationsApiHandlersMap;

  constructor(
    private readonly workspaceApi: WorkspaceApi,
    private readonly i18n = new I18n(
      notificationsApiVsCodeI18nDefaults,
      notificationsApiVsCodeI18nDictionaries,
      vscode.env.language
    )
  ) {
    this.strategies = {
      PROBLEM: new ProblemsTabNotificationHandler(),
      ALERT: new PopupMessagesNotificationHandler(this.workspaceApi, this.i18n),
    };
  }

  public kogitoNotifications_createNotification(notification: Notification): void {
    this.handle(notification).kogitoNotifications_createNotification(notification);
  }

  public kogitoNotifications_setNotifications(path: string, notifications: Notification[]): void {
    const alerts = notifications.filter((n) => n.type === "ALERT");
    const problems = notifications.filter((n) => n.type !== "ALERT");

    this.get("PROBLEM").kogitoNotifications_setNotifications(path, problems);
    this.get("ALERT").kogitoNotifications_setNotifications(path, alerts);
  }

  public kogitoNotifications_removeNotifications(path: string): void {
    this.get("PROBLEM").kogitoNotifications_removeNotifications(path);
  }

  private handle(notification: Notification): NotificationsApi {
    return this.get(notification.type);
  }

  private get(type: NotificationType): NotificationsApi {
    return this.strategies[type] ?? new ProblemsTabNotificationHandler();
  }
}
