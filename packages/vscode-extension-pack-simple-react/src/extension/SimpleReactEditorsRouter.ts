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

import { Router } from "@kogito-tooling/core-api";
import * as vscode from "vscode";
import * as __path from "path";
import { SimpleReactEditorsLanguageData } from "../common/SimpleReactEditorsLanguageData";

export class SimpleReactEditorsRouter extends Router {
  private readonly context: vscode.ExtensionContext;

  constructor(context: vscode.ExtensionContext) {
    super({
      getRoutes: () =>
        new Map<string, SimpleReactEditorsLanguageData>([["dmn", { type: "react" }], ["bpmn", { type: "react" }]])
    });
    this.context = context;
  }

  public getRelativePathTo(uri: string) {
    return vscode.Uri.file(__path.join(this.context.extensionPath, ...uri.split("/")))
      .with({ scheme: "vscode-resource" })
      .toString();
  }

  public getLanguageData(fileExtension: string) {
    return this.getLanguageDataByFileExtension().get(fileExtension);
  }

  public getTargetOrigin(): string {
    throw new Error("VSCode Simple React should not depend on external resources");
  }
}
