/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import * as vscode from "vscode";

export async function askForServiceRegistryUrl(args: { currentValue: string | undefined }) {
  return vscode.window
    .showInputBox({
      ignoreFocusOut: true,
      title: "Serverless Workflow Editor",
      prompt:
        "Provide the Service Registry URL to import functions.\n\nThat's the 'Core Registry API' URL you see on the Connection menu inside the Service Registry instance.",
      value: args.currentValue?.toString(),
      valueSelection: undefined, // Select everything
      validateInput(value: string) {
        try {
          new URL(value);
          return undefined;
        } catch (e) {
          return "Error: The provided input is not a valid Service Registry URL.";
        }
      },
    })
    .then((urlString) => {
      if (!urlString) {
        return;
      }
      return urlString;
    });
}

export function getServiceFileNameFromSwfServiceCatalogServiceId(swfServiceCatalogServiceId: string) {
  return `${swfServiceCatalogServiceId}__latest.yaml`;
}
