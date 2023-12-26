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

import { Notification, NotificationsChannelApi, NotificationType } from "../api";
import { WorkspaceChannelApi } from "@kie-tools-core/workspace/dist/api";
import { PopupMessagesNotificationHandler } from "./PopupMessagesNotificationHandler";
import { ProblemsTabNotificationHandler } from "./ProblemsTabNotificationHandler";
import { I18n } from "@kie-tools-core/i18n/dist/core";
import * as vscode from "vscode";
import { notificationsApiVsCodeI18nDefaults, notificationsApiVsCodeI18nDictionaries } from "./i18n";

type NotificationsApiHandlersMap = {
  [K in NotificationType]: NotificationsChannelApi;
};

export class VsCodeNotificationsChannelApiImpl implements NotificationsChannelApi {
  private readonly strategies: NotificationsApiHandlersMap;

  constructor(
    private readonly workspaceApi: WorkspaceChannelApi,
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

  public kogitoNotifications_setNotifications(
    pathRelativeToTheWorkspaceRoot: string,
    notifications: Notification[]
  ): void {
    const alerts = notifications.filter((n) => n.type === "ALERT");
    const problems = notifications.filter((n) => n.type !== "ALERT");

    this.get("PROBLEM").kogitoNotifications_setNotifications(pathRelativeToTheWorkspaceRoot, problems);
    this.get("ALERT").kogitoNotifications_setNotifications(pathRelativeToTheWorkspaceRoot, alerts);
  }

  public kogitoNotifications_removeNotifications(pathRelativeToTheWorkspaceRoot: string): void {
    this.get("PROBLEM").kogitoNotifications_removeNotifications(pathRelativeToTheWorkspaceRoot);
  }

  private handle(notification: Notification): NotificationsChannelApi {
    return this.get(notification.type);
  }

  private get(type: NotificationType): NotificationsChannelApi {
    return this.strategies[type] ?? new ProblemsTabNotificationHandler();
  }
}
