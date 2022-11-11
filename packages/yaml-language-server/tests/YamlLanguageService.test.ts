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
  getLanguageService,
  LanguageService,
  LanguageSettings,
  SchemaRequestService,
  SettingsState,
  Telemetry,
  WorkspaceContextService,
} from "@kie-tools/yaml-language-server";
import { TextDocument } from "vscode-json-languageservice";
import { Connection } from "vscode-languageserver/node";
import { SW_SPEC_WORKFLOW_SCHEMA } from "./workflow";

describe("YAMLValidation tests", () => {
  const schemaRequestService: SchemaRequestService = async (uri: string) => {
    if (uri === SW_SPEC_WORKFLOW_SCHEMA.$id) {
      return Promise.resolve(JSON.stringify(SW_SPEC_WORKFLOW_SCHEMA));
    } else {
      throw new Error(`Unable to load schema from '${uri}'`);
    }
  };
  const workspaceContext: WorkspaceContextService = {
    resolveRelativePath: (_relativePath: string, _resource: string) => {
      return "";
    },
  };

  const connection = {} as Connection;
  connection.onRequest = () => null;
  const telemetry = new Telemetry(connection);

  const yamlSettings = { yamlFormatterSettings: { enable: false } } as SettingsState;
  const yamlLanguageSettings: LanguageSettings = {
    validate: true,
    completion: false,
    format: false,
    hover: false,
    isKubernetes: false,
    schemas: [{ fileMatch: ["*.sw.yaml", "*.sw.yml"], uri: SW_SPEC_WORKFLOW_SCHEMA.$id }],
  };
  let yamlLs: LanguageService;

  beforeEach(() => {
    yamlLs = getLanguageService(schemaRequestService, workspaceContext, connection, telemetry, yamlSettings);
    yamlLs.configure(yamlLanguageSettings);
  });

  test.each([
    [
      "not valid YAML",
      "notValid.sw.yaml",
      `id: hello_world
specVersion: "0.1"
states: Wrong states type`,
    ],
    [
      "valid",
      "valid.sw.yaml",
      `id: hello_world
specVersion: "0.1"
start: Inject Hello World
states:
  - name: Inject Hello World
    type: inject
    data: {}
    end: true`,
    ],
  ])("%s", async (_description, uri, content) => {
    const textDocument = TextDocument.create(uri, "yaml", 1, content);
    const diagnostic = await yamlLs.doValidation(textDocument, false);

    expect(diagnostic.length).toMatchSnapshot();
    expect(diagnostic).toMatchSnapshot();
  });
});
