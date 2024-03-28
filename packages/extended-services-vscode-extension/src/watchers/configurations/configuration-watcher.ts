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
import { Configuration } from "./configuration";

export class ConfigurationWatcher {
  private configurationChangedHandler: ConfigurationChangedHandler | null = null;
  private configurationChangeListener: vscode.Disposable | undefined;

  constructor() {
    this.configurationChangeListener = vscode.workspace.onDidChangeConfiguration(this.handleConfigurationChange, this);
  }

  private handleConfigurationChange(configurationChange: vscode.ConfigurationChangeEvent) {
    const autoRunChanged = configurationChange.affectsConfiguration(Configuration.autoRunConfigurationID);
    const changedServiceURLChanged = configurationChange.affectsConfiguration(Configuration.serviceURLConfigurationID);
    const changedConnectionHeartbeatIntervalChanged = configurationChange.affectsConfiguration(
      Configuration.connectionHeartbeatIntervalConfigurationID
    );
    if (autoRunChanged || changedServiceURLChanged || changedConnectionHeartbeatIntervalChanged) {
      this.fireConfigurationChangedEvent();
    }
  }

  private fireConfigurationChangedEvent() {
    if (this.configurationChangedHandler) {
      this.configurationChangedHandler();
    }
  }

  public subscribeSettingsChanged(handler: ConfigurationChangedHandler) {
    this.configurationChangedHandler = handler;
  }

  public unsubscribeSettingsChanged() {
    this.configurationChangedHandler = null;
  }

  public dispose(): void {
    if (this.configurationChangeListener) {
      this.configurationChangeListener.dispose();
    }
    this.unsubscribeSettingsChanged();
  }
}

interface ConfigurationChangedHandler {
  (): void;
}
