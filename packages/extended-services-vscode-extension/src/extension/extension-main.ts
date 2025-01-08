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
import { LocalExtendedServices } from "../LocalExtendedServices";

const startExtendedServicesCommandUID: string = "extended-services-vscode-extension.startExtendedServices";
const stopExtendedServicesCommandUID: string = "extended-services-vscode-extension.stopExtendedServices";
const connectedEnablementUID: string = "extended-services-vscode-extension.connected";

let connectExtendedServicesCommand: vscode.Disposable;
let disconnectExtendedServicesCommand: vscode.Disposable;

let statusBarItem: vscode.StatusBarItem;
let diagnosticCollection: vscode.DiagnosticCollection;
let outputChannel: vscode.OutputChannel;

let kieFilesWatcher: KieFilesWatcher;
let configurationWatcher: ConfigurationWatcher;
let connection: Connection;
let localService: LocalExtendedServices;

/* Determines if the extension is connected with the Extended Services Backend */
let isConnected: boolean = false;
/* Determines the user explicitely disconnected the Extension from the Extended Services Backend  */
let disconnectedByUser: boolean = false;
let configuration: Configuration | null;

function initializeCommands(context: vscode.ExtensionContext) {
  connectExtendedServicesCommand = vscode.commands.registerCommand(startExtendedServicesCommandUID, () => {
    disconnectedByUser = false;
    startExtendedServices(context);
  });
  disconnectExtendedServicesCommand = vscode.commands.registerCommand(stopExtendedServicesCommandUID, () => {
    disconnectedByUser = true;
    if (configuration) {
      stopExtendedServices(configuration);
    }
  });
}

function initializeVSCodeElements() {
  vscode.commands.executeCommand("setContext", connectedEnablementUID, false);

  statusBarItem = vscode.window.createStatusBarItem(vscode.StatusBarAlignment.Right, 100);
  statusBarItem.hide();

  outputChannel = vscode.window.createOutputChannel("Extended Services VS Code Extension");

  diagnosticCollection = vscode.languages.createDiagnosticCollection("KIE Files Diagnostics");
}

function startExtendedServices(context: vscode.ExtensionContext): void {
  console.debug("[Extended Services Extension] Starting Extended Service");
  statusBarItem.command = undefined;
  try {
    configuration = fetchConfiguration();
  } catch (error) {
    console.error(`[Extended Services Extension] Extension configuration is wrong: ${error.message}`);
    vscode.window.showErrorMessage(
      `Extension configuration is wrong: ${error.message}. Please fix your local extension's setting.`
    );
    statusBarItem.hide();
    return;
  }

  if (configuration.enableAutoRun) {
    startLocalExtendedServices(configuration, context);
  } else {
    startConnection(configuration);
  }
}

function stopExtendedServices(configuration: Configuration | null) {
  console.debug("[Extended Services Extension] Stopping Extended Service");
  /* Invalidating immediatly the current connection, so any request coming when shutting down is not served */
  isConnected = false;
  statusBarItem.command = undefined;
  if (configuration?.enableAutoRun) {
    localService.stop();
  } else {
    connection.stop();
  }
}

function startLocalExtendedServices(configuration: Configuration, context: vscode.ExtensionContext): void {
  console.debug("[Extended Services Extension] Starting a Local Extended Services process");
  try {
    localService.start(configuration.extendedServicesURL, context.extensionPath);
  } catch (error) {
    stopExtendedServices(configuration);
    console.error(
      `[Extended Services Extension] An error happened while trying to start the Local Extended Services process: ${error.message}`
    );
    vscode.window.showErrorMessage(
      `An error happened while trying to start the Local Extended Services process: ${error.message}`
    );
  }
}

function startConnection(configuration: Configuration) {
  console.debug(
    `[Extended Services Extension] Connecting with the Extended Services located: ${configuration.extendedServicesURL}`
  );
  try {
    connection.start(configuration.extendedServicesURL, configuration.connectionHeartbeatIntervalInSecs);
  } catch (error) {
    stopExtendedServices(configuration);
    console.error(
      `[Extended Services Extension] An error happened while trying to connect to the service: ${error.message}`
    );
    vscode.window.showErrorMessage(`An error happened while trying to connect to the service: ${error.message}`);
  }
}

async function validate(extendedServicesURL: URL) {
  console.debug("[Extended Services Extension] Validating the opened KIE files");

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
  localService = new LocalExtendedServices();
  connection = new Connection();

  configurationWatcher.subscribeSettingsChanged(() => {
    stopExtendedServices(configuration);
    if (!disconnectedByUser && kieFilesWatcher.watchedKieFiles.length > 0) {
      startExtendedServices(context);
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
      startExtendedServices(context);
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

  localService.subscribeLocalExtendedServicesStarted(() => {
    console.debug("[Extended Services Extension] Local instance of Extended Services started");
    if (configuration) {
      startConnection(configuration);
    }
  });

  localService.subscribeLocalExtendedServicesOutputChanged((output: string) => {
    outputChannel.append(output);
  });

  localService.subscribeLocalExtendedServicesErrorOutputChanged((output: string) => {
    outputChannel.append(output);
    outputChannel.show();
  });

  localService.subscribeLocalExtendedServicesStopped(() => {
    console.debug("[Extended Services Extension] Local instance of Extended Services stopped");
    connection.stop();
  });

  connection.subscribeConnected(() => {
    console.debug("[Extended Services Extension] Connected with Extended Services");
    isConnected = true;
    vscode.commands.executeCommand("setContext", connectedEnablementUID, true);
    statusBarItem.show();
    if (configuration) {
      validate(configuration.extendedServicesURL);
      statusBarItem.text = "$(extended-services-connected)";
      statusBarItem.tooltip = "Apache KIE™ Extended Services are connected. Click to disconnect.";
      statusBarItem.command = stopExtendedServicesCommandUID;
    }
  });

  connection.subscribeConnectionLost((errorMessage: string) => {
    statusBarItem.hide();
    stopExtendedServices(configuration);
    isConnected = false;
    diagnosticCollection.clear();
    console.error("[Extended Services Extension] Connection lost with Extended Services");
    vscode.window.showErrorMessage(`Connection error: ${errorMessage}`);
  });

  connection.subscribeDisconnected(() => {
    console.debug("[Extended Services Extension] Disconnected with Extended Services");
    isConnected = false;
    vscode.commands.executeCommand("setContext", connectedEnablementUID, false);
    statusBarItem.text = "$(extended-services-disconnected)";
    statusBarItem.tooltip = "Apache KIE™ Extended Services are not connected. Click to connect.";
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
  outputChannel.dispose();

  localService.dispose();
  connection.dispose();
  kieFilesWatcher.dispose();
  configurationWatcher.dispose();
}
