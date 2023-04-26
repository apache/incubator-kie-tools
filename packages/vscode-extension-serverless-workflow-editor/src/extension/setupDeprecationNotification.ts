/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as vscode from "vscode";
import { COMMAND_IDS } from "./commandIds";
import { SwfVsCodeExtensionConfiguration } from "./configuration";

const NOTIFICATION_OPTIONS = {
  checkNewExtension: "Check new extension",
  dontShowAgain: "Don't show again",
};

export function setupDeprecationNotification(args: {
  context: vscode.ExtensionContext;
  configuration: SwfVsCodeExtensionConfiguration;
}) {
  args.context.subscriptions.push(
    vscode.commands.registerCommand(COMMAND_IDS.showDeprecationNotification, async () => {
      const deprecatedNotificationSelection = await vscode.window.showInformationMessage(
        "The Kogito Serverless Workflow Editor extension is now being published and updated by KIE. Please consider migrating to it to keep up with the latest features.",
        NOTIFICATION_OPTIONS.checkNewExtension,
        NOTIFICATION_OPTIONS.dontShowAgain
      );

      if (deprecatedNotificationSelection == NOTIFICATION_OPTIONS.checkNewExtension) {
        await vscode.commands.executeCommand("extension.open", "kie-group.serverless-workflow-vscode-extension");
      } else if (deprecatedNotificationSelection == NOTIFICATION_OPTIONS.dontShowAgain) {
        await args.configuration.configureDeprecationNotificationDisabled(false);
      }
    })
  );

  if (args.configuration.isDeprecationNotificationEnabled()) {
    vscode.commands.executeCommand(COMMAND_IDS.showDeprecationNotification);
  }
}
