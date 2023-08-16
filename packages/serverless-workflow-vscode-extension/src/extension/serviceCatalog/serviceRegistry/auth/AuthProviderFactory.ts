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
import { AuthProviderType } from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import { AuthProvider } from "./AuthProvider";
import { RhhccAuthProvider } from "./RhhccAuthProvider";
import { NoOpAuthProvider } from "./NoOpAuthProvider";
import { RedHatAuthExtensionStateStore } from "../../../RedHatAuthExtensionStateStore";

export class AuthProviderFactory {
  public constructor(
    private readonly args: {
      redhatAuthExtensionStateStore: RedHatAuthExtensionStateStore;
    }
  ) {}

  public lookupAuthProvider(args: {
    context: vscode.ExtensionContext;
    authProvider: AuthProviderType;
  }): AuthProvider | undefined {
    if (args.authProvider === AuthProviderType.NONE) {
      return new NoOpAuthProvider();
    }

    if (this.args.redhatAuthExtensionStateStore.isRedHatAuthExtensionEnabled) {
      return new RhhccAuthProvider(args.context);
    }

    return undefined;
  }
}
