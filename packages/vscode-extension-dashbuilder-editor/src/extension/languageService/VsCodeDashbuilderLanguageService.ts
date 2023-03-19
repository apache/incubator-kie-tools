/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
import { FileLanguage } from "@kie-tools/dashbuilder-language-service/dist/api";
import { DashbuilderLanguageService } from "@kie-tools/dashbuilder-language-service/dist/channel";

export class VsCodeDashbuilderLanguageService {
  private readonly dashbuilderLs: DashbuilderLanguageService;
  constructor() {
    this.dashbuilderLs = new DashbuilderLanguageService();
  }

  private getFileLanguage = (fileName: string): FileLanguage | null => {
    if (/\.dash\.(yml|yaml)$/i.test(fileName)) {
      return FileLanguage.YAML;
    }
    return null;
  };

  public getLs(document: vscode.TextDocument): DashbuilderLanguageService {
    const fileLanguage = this.getFileLanguage(document.fileName);
    if (fileLanguage === FileLanguage.YAML) {
      return new DashbuilderLanguageService();
    } else {
      throw new Error(`Could not determine LS for ${document.fileName}`);
    }
  }

  public dispose() {
    this.dashbuilderLs.dispose();
    return;
  }
}
