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

export class RedHatAuthExtensionStateStore implements vscode.Disposable {
  private readonly subscriptions = new Set<() => void>();
  private readonly extensionsChangeCallback: vscode.Disposable;

  private _isRedHatAuthExtensionEnabled: boolean;

  public constructor() {
    this._isRedHatAuthExtensionEnabled = this.isRedHatAuthExtensionCurrentlyEnabled();

    this.extensionsChangeCallback = vscode.extensions.onDidChange(() => {
      if (this._isRedHatAuthExtensionEnabled !== this.isRedHatAuthExtensionCurrentlyEnabled()) {
        this._isRedHatAuthExtensionEnabled = !this._isRedHatAuthExtensionEnabled;
        this.subscriptions.forEach((subscription) => subscription());
      }
    });
  }

  public subscribeToRedHatAuthExtensionStateChange(subscription: () => void): vscode.Disposable {
    this.subscriptions.add(subscription);
    return new vscode.Disposable(() => this.subscriptions.delete(subscription));
  }

  public get isRedHatAuthExtensionEnabled(): boolean {
    return this._isRedHatAuthExtensionEnabled;
  }

  public dispose() {
    this.subscriptions.clear();
    this.extensionsChangeCallback.dispose();
  }

  private isRedHatAuthExtensionCurrentlyEnabled(): boolean {
    return vscode.extensions.getExtension("redhat.vscode-redhat-account") !== undefined;
  }
}
