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

import * as vscode from "vscode";

export class VsCodeRecommendation {
  public static showExtendedServicesRecommendation(context: vscode.ExtensionContext) {
    const message =
      "There is another extension available that might help you.\n" +
      "Click [here](command:vscode-extension.installExtendedServices) to install it.";
    const action = "Install Apache KIE™ Extended Services for VS Code";

    const disposable = vscode.commands.registerCommand("vscode-extension.installExtendedServices", () => {
      vscode.env.openExternal(vscode.Uri.parse("vscode:extension/kie-group.extended-services-vscode-extension"));
    });

    vscode.window.showInformationMessage(message, action).then((selection) => {
      if (selection === action) {
        vscode.commands.executeCommand("vscode-extension.installExtendedServices");
      }
    });

    context.subscriptions.push(disposable);
  }
}
