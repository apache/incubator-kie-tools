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
import { AuthProviderType } from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import { AuthProvider } from "./AuthProvider";
import { RHCCAuthProvider } from "./RHCCAuthProvider";
import { NoOpAuthProvider } from "./NoOpAuthProvider";

export class AuthProviderFactory {
  private _rhAuthEnabled: boolean;

  constructor(
    private readonly args: {
      context: vscode.ExtensionContext;
      onExtensionChange: () => void;
    }
  ) {
    this._rhAuthEnabled = this.getCurrentRHExtensionEnabled();

    this.args.context.subscriptions.push(
      vscode.extensions.onDidChange((e) => {
        if (this._rhAuthEnabled !== this.getCurrentRHExtensionEnabled()) {
          this._rhAuthEnabled = !this._rhAuthEnabled;
          this.args.onExtensionChange();
        }
      })
    );
  }

  public get isRHAuthEnabled(): boolean {
    return this._rhAuthEnabled;
  }

  public lookupAuthProvider(args: { context: vscode.ExtensionContext; authProvider: AuthProviderType }): AuthProvider {
    if (args.authProvider === AuthProviderType.RH_ACCOUNT && this.isRHAuthEnabled) {
      return new RHCCAuthProvider(args.context);
    }
    return new NoOpAuthProvider();
  }

  private getCurrentRHExtensionEnabled(): boolean {
    return vscode.extensions.getExtension("redhat.vscode-redhat-account") !== undefined;
  }
}
