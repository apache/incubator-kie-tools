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

import { AuthProvider } from "./AuthProvider";
import * as vscode from "vscode";

const RH_ACCOUNT_PROVIDER_ID = "redhat-account-auth";
const RH_ACCOUNT_SCOPE = "openid";

export class RhhccAuthProvider implements AuthProvider {
  private session: vscode.AuthenticationSession | undefined;
  private subscriptions = new Set<() => void>();

  constructor(context: vscode.ExtensionContext) {
    context.subscriptions.push(
      vscode.authentication.onDidChangeSessions(async (e) => {
        if (e.provider.id === RH_ACCOUNT_PROVIDER_ID) {
          await this.acquireSession();
        }
      })
    );
    this.acquireSession();
  }

  public async getAuthHeader(): Promise<any> {
    if (this.session) {
      return Promise.resolve({
        Authorization: `Bearer ${this.session.accessToken}`,
      });
    }
    return Promise.resolve(undefined);
  }

  public async login(): Promise<void> {
    try {
      this.session = await vscode.authentication.getSession(RH_ACCOUNT_PROVIDER_ID, [RH_ACCOUNT_SCOPE], {
        createIfNone: true,
      });
      return Promise.resolve();
    } catch (err) {
      return Promise.reject(err);
    }
  }

  public subscribeToSessionChange(subscription: () => void): vscode.Disposable {
    this.subscriptions.add(subscription);
    return new vscode.Disposable(() => {
      this.unsubscribeToSessionChange(subscription);
    });
  }

  public unsubscribeToSessionChange(subscription: () => void) {
    this.subscriptions.delete(subscription);
  }

  public shouldLogin(): boolean {
    return this.session == undefined;
  }

  private async acquireSession(): Promise<void> {
    this.session = await vscode.authentication.getSession(RH_ACCOUNT_PROVIDER_ID, [RH_ACCOUNT_SCOPE]);
    this.subscriptions.forEach((subscription) => subscription());
  }
}
