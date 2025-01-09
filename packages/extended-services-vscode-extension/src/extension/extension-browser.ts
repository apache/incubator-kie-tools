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

import * as kiefilesfetcher from "../kieFiles/KieFilesFetcher";
import * as vscode from "vscode";
import * as validator from "../Validator";
import { Configuration, fetchConfiguration } from "../configurations/Configuration";
import { ConfigurationWatcher } from "../configurations/ConfigurationWatcher";
import { Connection } from "../Connection";
import { KieFile } from "../kieFiles/KieFile";
import { KieFilesWatcher } from "../kieFiles/KieFilesWatcher";

const startExtendedServicesCommandUID: string = "extended-services-vscode-extension.startExtendedServices";
const stopExtendedServicesCommandUID: string = "extended-services-vscode-extension.stopExtendedServices";
const connectedEnablementUID: string = "extended-services-vscode-extension.connected";

let connectExtendedServicesCommand: vscode.Disposable;
let disconnectExtendedServicesCommand: vscode.Disposable;

let statusBarItem: vscode.StatusBarItem;
let diagnosticCollection: vscode.DiagnosticCollection;

let kieFilesWatcher: KieFilesWatcher;
let configurationWatcher: ConfigurationWatcher;
let connection: Connection;

let userDisconnected: boolean = false;
let configuration: Configuration | null = null;

function initializeCommands(context: vscode.ExtensionContext) {
  connectExtendedServicesCommand = vscode.commands.registerCommand(startExtendedServicesCommandUID, () => {
    userDisconnected = false;
    startExtendedServices();
  });
  disconnectExtendedServicesCommand = vscode.commands.registerCommand(stopExtendedServicesCommandUID, () => {
    userDisconnected = true;
    stopExtendedServices();
  });
}

function initializeVSCodeElements() {
  vscode.commands.executeCommand("setContext", connectedEnablementUID, false);

  statusBarItem = vscode.window.createStatusBarItem(vscode.StatusBarAlignment.Right, 100);
  statusBarItem.text = "$(extended-services-disconnected)";
  statusBarItem.tooltip = "Apache KIE™ Extended Services is not connected. \n" + "Click to connect.";
  statusBarItem.command = startExtendedServicesCommandUID;
  statusBarItem.hide();

  diagnosticCollection = vscode.languages.createDiagnosticCollection("KIE Files Diagnostics");
}

function startExtendedServices(): void {
  try {
    statusBarItem.show();
    configuration = fetchConfiguration();
  } catch (error) {
    stopExtendedServices();
    vscode.window.showErrorMessage("An error happened while trying to start the Extended Services: " + error.message);
    return;
  }

  try {
    connection.start(configuration.extendedServicesURL, configuration.connectionHeartbeatIntervalinSecs);
  } catch (error) {
    stopExtendedServices();
    vscode.window.showErrorMessage("An error happened while trying to connect to the service:" + error.message);
  }
}

function stopExtendedServices() {
  configuration = null;
  connection.stop();
}

async function validate(configuration: Configuration) {
  diagnosticCollection.clear();

  const bpmnFiles: KieFile[] = await kiefilesfetcher.findActiveKieFiles([kiefilesfetcher.bpmnDocumentFilter]);
  const dmnFiles: KieFile[] = await kiefilesfetcher.findActiveKieFiles([kiefilesfetcher.dmnDocumentFilter]);

  for (const bpmnFile of bpmnFiles) {
    try {
      const bpmnDiagnostics: vscode.Diagnostic[] = await validator.validateBPMN(
        configuration.extendedServicesURL,
        bpmnFile
      );
      diagnosticCollection.set(bpmnFile.uri, bpmnDiagnostics);
    } catch (error) {
      stopExtendedServices();
      vscode.window.showErrorMessage(
        "An error happened while trying to validate " + bpmnFile.uri.path + ": " + error.message
      );
    }
  }

  for (const dmnFile of dmnFiles) {
    try {
      const bpmnDiagnostics: vscode.Diagnostic[] = await validator.validateDMN(
        configuration.extendedServicesURL,
        dmnFile
      );
      diagnosticCollection.set(dmnFile.uri, bpmnDiagnostics);
    } catch (error) {
      stopExtendedServices();
      vscode.window.showErrorMessage(
        "An error happened while trying to validate " + dmnFile.uri.path + ": " + error.message
      );
    }
  }
}

export function activate(context: vscode.ExtensionContext) {
  configurationWatcher = new ConfigurationWatcher();
  kieFilesWatcher = new KieFilesWatcher();
  connection = new Connection();

  configurationWatcher.subscribeSettingsChanged(() => {
    stopExtendedServices();
    if (!userDisconnected && kieFilesWatcher.watchedKieFiles.length > 0) {
      startExtendedServices();
    }
  });

  kieFilesWatcher.subscribeKieFilesOpened(() => {
    statusBarItem.show();
    if (userDisconnected) {
      return;
    }

    if (configuration) {
      validate(configuration);
    } else {
      startExtendedServices();
    }
  });

  kieFilesWatcher.subscribeKieFileChanged(() => {
    if (configuration) {
      validate(configuration);
    }
  });

  kieFilesWatcher.subscribeKieFilesClosed(() => {
    if (kieFilesWatcher.watchedKieFiles.length === 0) {
      stopExtendedServices();
      statusBarItem.hide();
    } else if (configuration) {
      validate(configuration);
    }
  });

  connection.subscribeConnected(() => {
    vscode.commands.executeCommand("setContext", connectedEnablementUID, true);
    if (configuration) {
      validate(configuration);
      statusBarItem.text = "$(extended-services-connected)";
      statusBarItem.tooltip = "Apache KIE™ Extended Services is connected. Click to disconnect.";
      statusBarItem.command = stopExtendedServicesCommandUID;
    }
  });

  connection.subscribeConnectionLost((errorMessage: string) => {
    vscode.window.showErrorMessage("Connection error: " + errorMessage);
    stopExtendedServices();
  });

  connection.subscribeDisconnected(() => {
    vscode.commands.executeCommand("setContext", connectedEnablementUID, false);
    statusBarItem.text = "$(extended-services-disconnected)";
    statusBarItem.tooltip = "Apache KIE™ Extended Services is not connected. Click to connect.";
    statusBarItem.command = startExtendedServicesCommandUID;
    diagnosticCollection.clear();
  });

  initializeCommands(context);
  initializeVSCodeElements();

  kieFilesWatcher.updateWatchedKieFiles();
}

export function deactivate(): void {
  connectExtendedServicesCommand.dispose();
  disconnectExtendedServicesCommand.dispose();
  statusBarItem.dispose();
  connection.dispose();
  kieFilesWatcher.dispose();
  configurationWatcher.dispose();
}
