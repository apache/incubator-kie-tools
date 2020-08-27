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

import { CapabilityResponseStatus } from "@kogito-tooling/backend/dist/api";
import { ServiceId, TestResult, TestRunnerCapability } from "@kogito-tooling/backend/dist/channel-api";
import { WorkspaceApi } from "@kogito-tooling/channel-common-api";
import { I18n } from "@kogito-tooling/i18n/dist/core";
import * as vscode from "vscode";
import { VsCodeI18n } from "./i18n";
import { VsCodeBackendProxy } from "./VsCodeBackendProxy";

export async function runTestScenario(
  backendProxy: VsCodeBackendProxy,
  workspaceApi: WorkspaceApi,
  vsCodeI18n: I18n<VsCodeI18n>
) {
  const i18n = vsCodeI18n.getCurrent();

  try {
    const response = await backendProxy.withCapability(
      ServiceId.TEST_RUNNER,
      async (capability: TestRunnerCapability) =>
        vscode.window.withProgress(
          { location: vscode.ProgressLocation.Notification, title: i18n.runningTestScenarios },
          () =>
            capability.execute(
              vscode.workspace.workspaceFolders![0].uri.fsPath,
              "testscenario.KogitoScenarioJunitActivatorTest"
            )
        )
    );

    if (response.status === CapabilityResponseStatus.NOT_AVAILABLE) {
      vscode.window.showWarningMessage(response.message!);
      return;
    }

    const testResult = response.body! as TestResult;

    vscode.window
      .showInformationMessage(
        i18n.testScenarioSummary(testResult.tests, testResult.errors, testResult.skipped, testResult.failures),
        i18n.viewTestSummary
      )
      .then(selection => {
        if (!selection) {
          return;
        }

        workspaceApi.receive_openFile(testResult.filePath);
      });
  } catch (e) {
    vscode.window.showErrorMessage(e);
  }
}
