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

import { BackendI18n } from "..";

export const en: BackendI18n = {
  dontShowAgain: "Don't Show Again",
  installExtension: "Install",
  installBackendExtensionMessage:
    "Consider installing the backend extension to augment the capabilities of the editors.",
  viewTestSummary: "View summary",
  runningTestScenarios: "Running test scenarios ...",
  testScenarioSummary: (tests: number, errors: number, skipped: number, failures: number) =>
    `Completed execution of ${tests} tests (errors: ${errors}, skipped: ${skipped}, failures: ${failures})`,
};
