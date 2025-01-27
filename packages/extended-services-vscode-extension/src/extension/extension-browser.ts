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

/* Determines if the extension is connected with the Extended Services Backend */
let isConnected = false;
/* Determines the user explicitely disconnected the Extension from the Extended Services Backend  */
let disconnectedByUser: boolean = false;
let configuration: Configuration | null = null;

function initializeCommands(context: vscode.ExtensionContext) {
  connectExtendedServicesCommand = vscode.commands.registerCommand(startExtendedServicesCommandUID, () => {
    disconnectedByUser = false;
    startExtendedServices();
  });
  disconnectExtendedServicesCommand = vscode.commands.registerCommand(stopExtendedServicesCommandUID, () => {
    disconnectedByUser = true;
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
  console.debug("[Extended Services Extension] Starting Extended Service");
  try {
    configuration = fetchConfiguration();
  } catch (error) {
    console.error(`[Extended Services Extension] Extension configuration is wrong: ${error.message}`);
    vscode.window.showErrorMessage(
      `Extension configuration is wrong: ${error.message}. Please fix your local extension's setting`
    );
    return;
  }

  try {
    console.debug(
      `[Extended Services Extension] Connecting with the Extended Services located: ${configuration.extendedServicesURL}`
    );
    connection.start(configuration.extendedServicesURL, configuration.connectionHeartbeatIntervalInSecs);
  } catch (error) {
    stopExtendedServices();
    console.error(
      `[Extended Services Extension] An error happened while trying to start the local service: ${error.message}`
    );
    vscode.window.showErrorMessage(`An error happened while trying to start the local service: ${error.message}`);
  }
}

function stopExtendedServices() {
  console.debug("[Extended Services Extension] Stopping Extended Service");
  /* Invalidating immediatly the current connection, so any request coming when shutting down is not served */
  isConnected = false;
  configuration = null;
  connection.stop();
}

async function validate(extendedServicesURL: URL) {
  diagnosticCollection.clear();

  const bpmnFiles: KieFile[] = await kiefilesfetcher.findActiveKieFiles([kiefilesfetcher.bpmnDocumentFilter]);
  const dmnFiles: KieFile[] = await kiefilesfetcher.findActiveKieFiles([kiefilesfetcher.dmnDocumentFilter]);

  for (const bpmnFile of bpmnFiles) {
    try {
      console.debug(`[Extended Services Extension] Validating BPMN file: ${bpmnFile.uri.path}`);
      const bpmnDiagnostics: vscode.Diagnostic[] = await validator.validateBPMN(extendedServicesURL, bpmnFile);
      diagnosticCollection.set(bpmnFile.uri, bpmnDiagnostics);
    } catch (error) {
      console.error(
        `[Extended Services Extension] An error happened while trying to validate ${bpmnFile.uri.path}: ${error.message}`
      );
      vscode.window.showErrorMessage(
        `An error happened while trying to validate ${bpmnFile.uri.path}: ${error.message}`
      );
    }
  }

  for (const dmnFile of dmnFiles) {
    try {
      console.debug(`[Extended Services Extension] Validating DMN file: ${dmnFile.uri.path}`);
      const dmnDiagnostics: vscode.Diagnostic[] = await validator.validateDMN(extendedServicesURL, dmnFile);
      diagnosticCollection.set(dmnFile.uri, dmnDiagnostics);
    } catch (error) {
      console.error(
        `[Extended Services Extension] An error happened while trying to validate ${dmnFile.uri.path}: ${error.message}`
      );
      vscode.window.showErrorMessage(
        `An error happened while trying to validate ${dmnFile.uri.path}: ${error.message}`
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
    if (!disconnectedByUser && kieFilesWatcher.watchedKieFiles.length > 0) {
      startExtendedServices();
    }
  });

  kieFilesWatcher.subscribeKieFilesOpened(() => {
    console.debug(
      `[Extended Services Extension] A KIE file has been opened. Current opened KIE files: ${
        kieFilesWatcher.watchedKieFiles.length
      }`
    );
    if (!disconnectedByUser && isConnected && configuration) {
      validate(configuration.extendedServicesURL);
    }
    if (!disconnectedByUser && !isConnected) {
      startExtendedServices();
    }
  });

  kieFilesWatcher.subscribeKieFileChanged(() => {
    console.debug("[Extended Services Extension] A KIE file has been changed");
    if (!disconnectedByUser && isConnected && configuration) {
      validate(configuration.extendedServicesURL);
    }
  });

  kieFilesWatcher.subscribeKieFilesClosed(() => {
    console.debug(
      `[Extended Services Extension] A KIE file has been closed. Current opened KIE files: ${kieFilesWatcher.watchedKieFiles.length}`
    );
    if (!disconnectedByUser && isConnected && configuration) {
      validate(configuration.extendedServicesURL);
    }
  });

  connection.subscribeConnected(() => {
    console.debug("[Extended Services Extension] Connected with Extended Services");
    isConnected = true;
    vscode.commands.executeCommand("setContext", connectedEnablementUID, true);
    statusBarItem.show();
    if (configuration) {
      validate(configuration.extendedServicesURL);
      statusBarItem.text = "$(extended-services-connected)";
      statusBarItem.tooltip = "Apache KIE™ Extended Services is connected. Click to disconnect.";
      statusBarItem.command = stopExtendedServicesCommandUID;
    }
  });

  connection.subscribeConnectionLost((errorMessage: string) => {
    statusBarItem.hide();
    stopExtendedServices();
    isConnected = false;
    console.error("[Extended Services Extension] Connection lost with Extended Services");
    vscode.window.showErrorMessage(`Connection error: ${errorMessage}`);
  });

  connection.subscribeDisconnected(() => {
    console.debug("[Extended Services Extension] Disconnected with Extended Services");
    isConnected = false;
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
