/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { I18n } from "@kie-tooling-core/i18n/dist/core/I18n";
import { NotificationsApi } from "@kie-tooling-core/notifications/dist/api";
import { WorkspaceApi } from "@kie-tooling-core/workspace/dist/api";
import * as vscode from "vscode";
import { CapabilityResponseStatus } from "../../api";
import { ServiceId, TestResult, TestScenarioRunnerCapability } from "../../channel-api";
import { BackendI18n } from "../../i18n";
import { VsCodeBackendProxy } from "../VsCodeBackendProxy";

/**
 * Test scenario runner command registration on VS Code.
 * @param args.command Command associated with test scenario runner.
 * @param args.context The `vscode.ExtensionContext` provided on the activate method of the extension.
 * @param args.backendProxy The proxy between channels and available backend services.
 * @param args.backendI18n I18n for backend services.
 * @param args.workspaceApi Workspace API.
 */
export function registerTestScenarioRunnerCommand(args: {
  command: string;
  context: vscode.ExtensionContext;
  backendProxy: VsCodeBackendProxy;
  backendI18n: I18n<BackendI18n>;
  workspaceApi: WorkspaceApi;
  notificationsApi: NotificationsApi;
}) {
  args.context.subscriptions.push(
    vscode.commands.registerCommand(args.command, () => run(args.backendProxy, args.backendI18n, args.notificationsApi))
  );
}

async function run(
  backendProxy: VsCodeBackendProxy,
  backendI18n: I18n<BackendI18n>,
  notificationsApi: NotificationsApi
) {
  const i18n = backendI18n.getCurrent();

  try {
    const response = await backendProxy.withCapability(
      ServiceId.TEST_SCENARIO_RUNNER,
      async (capability: TestScenarioRunnerCapability) =>
        vscode.window.withProgress(
          { location: vscode.ProgressLocation.Notification, title: i18n.runningTestScenarios, cancellable: true },
          (_, token) => {
            token.onCancellationRequested(() => {
              capability.stopActiveExecution();
            });

            return capability.execute(
              vscode.workspace.workspaceFolders![0].uri.fsPath,
              "testscenario.KogitoScenarioJunitActivatorTest"
            );
          }
        )
    );

    if (response.status === CapabilityResponseStatus.NOT_AVAILABLE) {
      notificationsApi.kogitoNotifications_createNotification({
        message: response.message!,
        severity: "WARNING",
        type: "ALERT",
        path: "",
      });
      return;
    }

    if (!response.body) {
      return;
    }

    const testResult: TestResult = response.body;

    notificationsApi.kogitoNotifications_createNotification({
      message: i18n.testScenarioSummary(testResult.tests, testResult.errors, testResult.skipped, testResult.failures),
      severity: "INFO",
      type: "ALERT",
      path: testResult.filePath,
    });
  } catch (e) {
    notificationsApi.kogitoNotifications_createNotification({
      message: e,
      severity: "ERROR",
      type: "ALERT",
      path: "",
    });
  }
}
