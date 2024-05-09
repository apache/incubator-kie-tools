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
import * as conf from "../configurations/Configuration";
import * as cw from "../configurations/ConfigurationWatcher";
import * as conn from "../Connection";
import * as kw from "../kieFiles/KieFilesWatcher";
import * as ls from "../LocalExtendedServices";
import * as vldt from "../Validator";

const connectExtendedServicesCommandUID: string = "extended-services-vscode-extension.connectExtendedServices";
const disconnectExtendedServicesCommandUID: string = "extended-services-vscode-extension.disconnectExtendedServices";
const connectedEnablementUID: string = "extended-services-vscode-extension.connected";

let connectExtendedServicesCommand: vscode.Disposable;
let disconnectExtendedServicesCommand: vscode.Disposable;

let kieFileWatcher: kw.KieFilesWatcher;
let configurationWatcher: cw.ConfigurationWatcher;
let connection: conn.Connection;
let localService: ls.LocalExtendedServices;
let validator: vldt.Validator;
let statusBarItem: vscode.StatusBarItem;
let userDisconnected: boolean;
let connected: boolean;

function connectExtendedServices(): void {
    userDisconnected = false;
    const configuration = conf.fetchConfiguration();
    if (configuration) {
        if (configuration.enableAutoRun) {
            vscode.commands.executeCommand(localService.startLocalExtendedServicesCommandUID, configuration.extendedServicesURL);
        } else {
            vscode.commands.executeCommand(
                connection.startConnectionHeartbeatCommandUID,
                configuration.extendedServicesURL,
                configuration.connectionHeartbeatIntervalinSecs
            );
        }
    }
}

function disconnectExtendedServices(): void {
    userDisconnected = true;
    const configuration: conf.Configuration | undefined = conf.fetchConfiguration();
    if (configuration && configuration.enableAutoRun) {
        vscode.commands.executeCommand(localService.stopLocalExtendedServicesCommandUID);
    } else {
        vscode.commands.executeCommand(connection.stopConnectionHeartbeatCommandUID);
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
}

export async function activate(context: vscode.ExtensionContext): Promise<void> {
    vscode.commands.executeCommand("setContext", connectedEnablementUID, false);
    userDisconnected = false;
    connected = false;

    statusBarItem = vscode.window.createStatusBarItem(vscode.StatusBarAlignment.Right, 100);
    statusBarItem.show();

    configurationWatcher = new cw.ConfigurationWatcher();
    kieFileWatcher = await kw.KieFilesWatcher.create();
    localService = new ls.LocalExtendedServices();
    connection = new conn.Connection();
    validator = new vldt.Validator();

    initializeCommands(context);

    configurationWatcher.subscribeSettingsChanged(() => {
        if (!userDisconnected && connected) {
            const configuration: conf.Configuration | undefined = conf.fetchConfiguration();

            if (configuration && configuration.enableAutoRun) {
                vscode.commands.executeCommand(localService.stopLocalExtendedServicesCommandUID);
            } else {
                vscode.commands.executeCommand(connection.stopConnectionHeartbeatCommandUID);
            }

            if (configuration) {
                if (configuration.enableAutoRun) {
                    vscode.commands.executeCommand(
                        localService.startLocalExtendedServicesCommandUID,
                        configuration.extendedServicesURL
                    );
                } else {
                    vscode.commands.executeCommand(
                        connection.startConnectionHeartbeatCommandUID,
                        configuration.extendedServicesURL,
                        configuration.connectionHeartbeatIntervalinSecs
                    );
                }
            }
        }
    });

    kieFileWatcher.subscribeKieFilesOpened(() => {
        statusBarItem.show();
        if (!userDisconnected) {
            const configuration = conf.fetchConfiguration();
            if (configuration) {
                if (configuration.enableAutoRun) {
                    vscode.commands.executeCommand(localService.startLocalExtendedServicesCommandUID, configuration.extendedServicesURL);
                } else {
                    vscode.commands.executeCommand(
                        connection.startConnectionHeartbeatCommandUID,
                        configuration.extendedServicesURL,
                        configuration.connectionHeartbeatIntervalinSecs
                    );
                }
            }
        }
    });

    kieFileWatcher.subscribeKieFileChanged(() => {
        vscode.commands.executeCommand(validator.clearValidationCommandUID);
        const configuration: conf.Configuration | undefined = conf.fetchConfiguration();
        if (configuration && connected) {
            vscode.commands.executeCommand(validator.validateCommandUID, configuration.extendedServicesURL);
        }
    });

    kieFileWatcher.subscribeKieFilesClosed(() => {
        statusBarItem.hide();
        const configuration: conf.Configuration | undefined = conf.fetchConfiguration();
        if (configuration && configuration.enableAutoRun) {
            vscode.commands.executeCommand(localService.stopLocalExtendedServicesCommandUID);
        } else {
            vscode.commands.executeCommand(connection.stopConnectionHeartbeatCommandUID);
        }
    });

    localService.subscribeLocalExtendedServicesStarted(() => {
        const configuration: conf.Configuration | undefined = conf.fetchConfiguration();
        if (configuration) {
            vscode.commands.executeCommand(
                connection.startConnectionHeartbeatCommandUID,
                configuration.extendedServicesURL,
                configuration.connectionHeartbeatIntervalinSecs
            );
        }
    });

    localService.subscribeLocalExtendedServicesStopped(() => {
        vscode.commands.executeCommand(connection.stopConnectionHeartbeatCommandUID);
    });

    connection.subscribeConnected(() => {
        connected = true;
        vscode.commands.executeCommand("setContext", connectedEnablementUID, true);
        statusBarItem.text = "$(extended-services-connected)";
        statusBarItem.tooltip = "Apache KIE Extended Services are connected. \n" + "Click to disconnect.";
        statusBarItem.command = disconnectExtendedServicesCommandUID;
        vscode.commands.executeCommand(validator.clearValidationCommandUID);
        const configuration: conf.Configuration | undefined = conf.fetchConfiguration();
        if (configuration && connected) {
            vscode.commands.executeCommand(validator.validateCommandUID, configuration.extendedServicesURL);
        }
    });

    connection.subscribeDisconnected(() => {
        connected = false;
        vscode.commands.executeCommand("setContext", connectedEnablementUID, false);
        statusBarItem.text = "$(extended-services-disconnected)";
        statusBarItem.tooltip = "Apache KIE Extended Services are not connected. \n" + "Click to connect.";
        statusBarItem.command = connectExtendedServicesCommandUID;
        vscode.commands.executeCommand(validator.clearValidationCommandUID);
    });
}

export function deactivate(): void {
    connectExtendedServicesCommand.dispose();
    disconnectExtendedServicesCommand.dispose();
    validator.dispose();
    localService.dispose();
    connection.dispose();
    kieFileWatcher.dispose();
    configurationWatcher.dispose();
    statusBarItem.dispose();
}
