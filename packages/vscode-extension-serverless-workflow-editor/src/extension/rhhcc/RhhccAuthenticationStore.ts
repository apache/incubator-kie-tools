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

export class RhhccAuthenticationStore {
  private _session: vscode.AuthenticationSession | undefined;
  private subscriptions = new Set<(session: vscode.AuthenticationSession | undefined) => void>();

  public get session() {
    return this._session;
  }

  public setSession(session: vscode.AuthenticationSession | undefined) {
    this._session = session;
    this.subscriptions.forEach((subscription) => subscription(session));
  }

  public subscribeToSessionChange(
    subscription: (session: vscode.AuthenticationSession | undefined) => any
  ): vscode.Disposable {
    this.subscriptions.add(subscription);
    return new vscode.Disposable(() => {
      this.unsubscribeToSessionChange(subscription);
    });
  }

  public unsubscribeToSessionChange(subscription: (session: vscode.AuthenticationSession | undefined) => any) {
    this.subscriptions.delete(subscription);
  }
}
