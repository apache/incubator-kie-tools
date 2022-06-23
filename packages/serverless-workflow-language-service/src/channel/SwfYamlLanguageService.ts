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

import {
  CodeLens,
  CompletionItem,
  CompletionItemKind,
  InsertTextFormat,
  Position,
  Range,
} from "vscode-languageserver-types";
import * as jsonc from "jsonc-parser";
import { JSONPath } from "jsonc-parser";
import { posix as posixPath } from "path";
import {
  SwfServiceCatalogFunction,
  SwfServiceCatalogFunctionSourceType,
  SwfServiceCatalogService,
  SwfServiceCatalogServiceSourceType,
} from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import { SwfLanguageServiceCommandArgs, SwfLanguageServiceCommandExecution } from "../api";
import * as swfModelQueries from "./modelQueries";
import { Specification } from "@severlessworkflow/sdk-typescript";
import { SW_SPEC_WORKFLOW_SCHEMA } from "../schemas";
import { getLanguageService, JSONDocument, LanguageService, TextDocument } from "vscode-json-languageservice";
import * as ls from "vscode-languageserver-types";
import { SwfLanguageService, SwfLanguageServiceArgs } from "./SwfLanguageService";
import { FileLanguage } from "../editor";

export class SwfYamlLanguageService extends SwfLanguageService {
  fileLanguage = FileLanguage.YAML;
  fileMatch = ["*.sw.yaml", "*.sw.yml"];

  constructor(args: SwfLanguageServiceArgs) {
    super(args);
  }
}
