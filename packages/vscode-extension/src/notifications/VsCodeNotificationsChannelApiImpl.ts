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
import { Notification } from "@kie-tools-core/notifications/dist/api";
import * as vscode from "vscode";
import { VsCodeWorkspaceChannelApiImpl } from "../workspace/VsCodeWorkspaceChannelApiImpl";
import { PopupMessagesNotificationHandler } from "./PopupMessagesNotificationHandler";
import { ProblemsTabNotificationHandler } from "./ProblemsTabNotificationHandler";
import { notificationsApiVsCodeI18nDefaults, notificationsApiVsCodeI18nDictionaries } from "./i18n";
import { KogitoEditorDocument } from "../VsCodeKieEditorController";

export class VsCodeNotificationsChannelApiImpl {
  private readonly strategies;

  constructor(
    private readonly vscodeWorkspace: VsCodeWorkspaceChannelApiImpl,
    private readonly i18n = new I18n(
      notificationsApiVsCodeI18nDefaults,
      notificationsApiVsCodeI18nDictionaries,
      vscode.env.language
    )
  ) {
    this.strategies = {
      PROBLEM: new ProblemsTabNotificationHandler(),
      ALERT: new PopupMessagesNotificationHandler(this.vscodeWorkspace, this.i18n),
    };
  }

  public createNotification(document: KogitoEditorDocument["document"], notification: Notification): void {
    if (notification.type === "ALERT") {
      this.strategies.ALERT.createNotification(notification);
    } else if (notification.type === "PROBLEM") {
      this.strategies.PROBLEM.createNotification(document, notification);
    } else {
      throw new Error(`Unknown notification type ${notification.type}`);
    }
  }

  public setNotifications(
    document: KogitoEditorDocument["document"],
    normalizedPosixPathRelativeToTheWorkspaceRoot: string,
    notifications: Notification[]
  ): void {
    this.strategies.PROBLEM.setProblemsEntries(
      document,
      normalizedPosixPathRelativeToTheWorkspaceRoot,
      notifications.filter((n) => n.type === "PROBLEM")
    );

    this.strategies.ALERT.showAlert(
      normalizedPosixPathRelativeToTheWorkspaceRoot,
      notifications.filter((n) => n.type === "ALERT")
    );
  }

  public removeNotifications(
    document: KogitoEditorDocument["document"],
    normalizedPosixPathRelativeToTheWorkspaceRoot: string
  ): void {
    this.strategies.PROBLEM.removeNotifications(document, normalizedPosixPathRelativeToTheWorkspaceRoot);
  }
}
