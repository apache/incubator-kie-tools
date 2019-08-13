/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import { LanguageData, Router, Routes } from "appformer-js-core";
import * as vscode from "vscode";
import * as __path from "path";

export class KogitoRouter implements Router {
  private readonly context: vscode.ExtensionContext;
  private readonly languageDataByFileExtension: Map<string, LanguageData>;

  constructor(context: vscode.ExtensionContext, ...routesArray: Routes[]) {
    this.context = context;

    const allLanguageData = new Map<string, any>();
    routesArray.reduce((map, routes) => {
      routes.getRoutes(this).forEach((v, k) => map.set(k, v));
      return map;
    }, allLanguageData);
    this.languageDataByFileExtension = allLanguageData;
  }

  public getRelativePathTo(uri: string) {
    return vscode.Uri.file(__path.join(this.context.extensionPath, ...uri.split("/")))
      .with({ scheme: "vscode-resource" })
      .toString();
  }

  public getLanguageData(fileExtension: string) {
    return this.languageDataByFileExtension.get(fileExtension);
  }
}
