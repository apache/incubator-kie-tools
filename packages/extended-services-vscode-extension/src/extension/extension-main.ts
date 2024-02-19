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
import { Configuration } from "../watchers/configurations/configuration";
import { ConfigurationWatcher } from "../watchers/configurations/configuration-watcher";
import { ConnectionManager } from "../connection-manager";
import { KIEFileWatcher } from "../watchers/kie-files/kie-file-watcher";
import { LocalServiceManager } from "../local-service-manager";
import { ValidationHelper } from "../validation-helper";

const connectExtendedServicesCommandUID: string = "extended-services-vscode-extension.connectExtendedServices";
const disconnectExtendedServicesCommandUID: string = "extended-services-vscode-extension.disconnectExtendedServices";
const connectedEnablamentUID: string = "extended-services-vscode-extension.connected";

let connectExtendedServicesCommand: vscode.Disposable;
let disconnectExtendedServicesCommand: vscode.Disposable;

let kieFileWatcher: KIEFileWatcher;
let configurationWatcher: ConfigurationWatcher;
let connectionManager: ConnectionManager;
let localServiceManager: LocalServiceManager;
let validationHelper: ValidationHelper;
let statusBarItem: vscode.StatusBarItem;
let userDisconnected: boolean;
let connected: boolean;

function connectExtendedServices(): void {
  userDisconnected = false;
  const configuration = Configuration.fetchConfiguration();
  if (configuration) {
    if (configuration.autoRun) {
      vscode.commands.executeCommand(localServiceManager.startLocalServiceCommandUID, configuration.serviceURL);
    } else {
      vscode.commands.executeCommand(
        connectionManager.startConnectionHeartbeatCommandUID,
        configuration.serviceURL,
        configuration.connectionHeartbeatInterval
      );
    }
  }
}

function disconnectExtendedServices(): void {
  userDisconnected = true;
  const configuration: Configuration | undefined = Configuration.fetchConfiguration();
  if (configuration && configuration.autoRun) {
    vscode.commands.executeCommand(localServiceManager.stopLocalServiceCommandUID);
  } else {
    vscode.commands.executeCommand(connectionManager.stopConnectionHeartbeatCommandUID);
  }
}

function initializeCommands(context: vscode.ExtensionContext): void {
  const connectExtendedServicesCommandHandler = () => {
    connectExtendedServices();
  };

  const disconnectExtendedServicesCommandHandler = () => {
    disconnectExtendedServices();
  };

  connectExtendedServicesCommand = vscode.commands.registerCommand(
    connectExtendedServicesCommandUID,
    connectExtendedServicesCommandHandler
  );

  disconnectExtendedServicesCommand = vscode.commands.registerCommand(
    disconnectExtendedServicesCommandUID,
    disconnectExtendedServicesCommandHandler
  );

  context.subscriptions.push(connectExtendedServicesCommand);
  context.subscriptions.push(disconnectExtendedServicesCommand);
}

export function activate(context: vscode.ExtensionContext): void {
  vscode.commands.executeCommand("setContext", connectedEnablamentUID, false);
  userDisconnected = false;
  connected = false;

  statusBarItem = vscode.window.createStatusBarItem(vscode.StatusBarAlignment.Right, 100);
  statusBarItem.show();
  context.subscriptions.push(statusBarItem);

  kieFileWatcher = new KIEFileWatcher();
  configurationWatcher = new ConfigurationWatcher();
  localServiceManager = new LocalServiceManager(context);
  connectionManager = new ConnectionManager(context);
  validationHelper = new ValidationHelper(context);

  initializeCommands(context);

  configurationWatcher.subscribeSettingsChanged(() => {
    if (!userDisconnected && connected) {
      const configuration: Configuration | undefined = Configuration.fetchConfiguration();

      if (configuration && configuration.autoRun) {
        vscode.commands.executeCommand(localServiceManager.stopLocalServiceCommandUID);
      } else {
        vscode.commands.executeCommand(connectionManager.stopConnectionHeartbeatCommandUID);
      }

      if (configuration) {
        if (configuration.autoRun) {
          vscode.commands.executeCommand(localServiceManager.startLocalServiceCommandUID, configuration.serviceURL);
        } else {
          vscode.commands.executeCommand(
            connectionManager.startConnectionHeartbeatCommandUID,
            configuration.serviceURL,
            configuration.connectionHeartbeatInterval
          );
        }
      }
    }
  });

  kieFileWatcher.subscribeKIEFilesOpened(() => {
    statusBarItem.show();
    if (!userDisconnected) {
      const configuration = Configuration.fetchConfiguration();
      if (configuration) {
        if (configuration.autoRun) {
          vscode.commands.executeCommand(localServiceManager.startLocalServiceCommandUID, configuration.serviceURL);
        } else {
          vscode.commands.executeCommand(
            connectionManager.startConnectionHeartbeatCommandUID,
            configuration.serviceURL,
            configuration.connectionHeartbeatInterval
          );
        }
      }
    }
  });

  kieFileWatcher.subscribeKIEFilesChanged(() => {
    vscode.commands.executeCommand(validationHelper.clearValidationCommandUID);
    const configuration: Configuration | undefined = Configuration.fetchConfiguration();
    if (configuration && connected) {
      vscode.commands.executeCommand(validationHelper.validateCommandUID, configuration.serviceURL);
    }
  });

  kieFileWatcher.subscribeKIEFilesClosed(() => {
    statusBarItem.hide();
    const configuration: Configuration | undefined = Configuration.fetchConfiguration();
    if (configuration && configuration.autoRun) {
      vscode.commands.executeCommand(localServiceManager.stopLocalServiceCommandUID);
    } else {
      vscode.commands.executeCommand(connectionManager.stopConnectionHeartbeatCommandUID);
    }
  });

  localServiceManager.subscribeLocalServicesRunning(() => {
    const configuration: Configuration | undefined = Configuration.fetchConfiguration();
    if (configuration) {
      vscode.commands.executeCommand(
        connectionManager.startConnectionHeartbeatCommandUID,
        configuration.serviceURL,
        configuration.connectionHeartbeatInterval
      );
    }
  });

  localServiceManager.subscribeLocalServicesTerminated(() => {
    vscode.commands.executeCommand(connectionManager.stopConnectionHeartbeatCommandUID);
  });

  connectionManager.subscribeConnected(() => {
    connected = true;
    vscode.commands.executeCommand("setContext", connectedEnablamentUID, true);
    statusBarItem.text = "$(extended-services-connected)";
    statusBarItem.tooltip = "Apache KIE Extended Services are connected. \n" + "Click to disconnect.";
    statusBarItem.command = disconnectExtendedServicesCommandUID;
    vscode.commands.executeCommand(validationHelper.clearValidationCommandUID);
    const configuration: Configuration | undefined = Configuration.fetchConfiguration();
    if (configuration && connected) {
      vscode.commands.executeCommand(validationHelper.validateCommandUID, configuration.serviceURL);
    }
  });

  connectionManager.subscribeDisconnected(() => {
    connected = false;
    vscode.commands.executeCommand("setContext", connectedEnablamentUID, false);
    statusBarItem.text = "$(extended-services-disconnected)";
    statusBarItem.tooltip = "Apache KIE Extended Services are not connected. \n" + "Click to connect.";
    statusBarItem.command = connectExtendedServicesCommandUID;
    vscode.commands.executeCommand(validationHelper.clearValidationCommandUID);
  });
}

export function deactivate(): void {
  connectExtendedServicesCommand.dispose();
  disconnectExtendedServicesCommand.dispose();
  validationHelper.dispose();
  localServiceManager.dispose();
  connectionManager.dispose();
  kieFileWatcher.dispose();
  configurationWatcher.dispose();
  statusBarItem.dispose();
}
