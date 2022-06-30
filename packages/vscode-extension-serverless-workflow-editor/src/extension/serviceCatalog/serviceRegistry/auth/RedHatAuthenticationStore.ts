/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as vscode from "vscode";

export class RedHatAuthenticationStore implements vscode.Disposable {
  private static instance = new RedHatAuthenticationStore();

  private readonly subscriptions = new Set<() => void>();
  private readonly disposeCallback: vscode.Disposable;

  private _rhAuthEnabled: boolean;

  private constructor() {
    this._rhAuthEnabled = this.getCurrentRHExtensionEnabled();

    this.disposeCallback = vscode.extensions.onDidChange(() => {
      if (this._rhAuthEnabled !== this.getCurrentRHExtensionEnabled()) {
        this._rhAuthEnabled = !this._rhAuthEnabled;
        this.subscriptions.forEach((subscription) => subscription());
      }
    });
  }

  public subscribeToRedHatAuthenticationStateChange(subscription: () => void): vscode.Disposable {
    this.subscriptions.add(subscription);
    return new vscode.Disposable(() => this.subscriptions.delete(subscription));
  }

  public get isRHAuthEnabled(): boolean {
    return this._rhAuthEnabled;
  }

  public dispose() {
    this.subscriptions.clear();
    this.disposeCallback.dispose();
  }

  private getCurrentRHExtensionEnabled(): boolean {
    return vscode.extensions.getExtension("redhat.vscode-redhat-account") !== undefined;
  }

  public static get() {
    if (this.instance === undefined) {
      this.instance = new RedHatAuthenticationStore();
    }
    return this.instance;
  }
}
