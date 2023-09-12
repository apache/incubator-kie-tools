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

import { DashbuilderLanguageServiceChannelApi } from "@kie-tools/dashbuilder-language-service/dist/api";
import { DashbuilderYamlLanguageService } from "@kie-tools/dashbuilder-language-service/dist/channel";
import { CodeLens, CompletionItem, Position, Range } from "vscode-languageserver-types";

export class DashbuilderLanguageServiceChannelApiImpl implements DashbuilderLanguageServiceChannelApi {
  constructor(private readonly ls: DashbuilderYamlLanguageService) {}

  public async kogitoDashbuilderLanguageService__getCompletionItems(args: {
    content: string;
    uri: string;
    cursorPosition: Position;
    cursorWordRange: Range;
  }): Promise<CompletionItem[]> {
    return await this.ls.getCompletionItems(args);
  }

  public async kogitoDashbuilderLanguageService__getCodeLenses(args: {
    uri: string;
    content: string;
  }): Promise<CodeLens[]> {
    return await this.ls.getCodeLenses(args);
  }
}
