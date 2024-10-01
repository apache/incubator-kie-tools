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

import { backendI18nDefaults, backendI18nDictionaries } from "@kie-tools-core/backend/dist/i18n";
import { EditorEnvelopeLocator, EnvelopeContentType, EnvelopeMapping } from "@kie-tools-core/editor/dist/api";
import { I18n } from "@kie-tools-core/i18n/dist/core";
import { VsCodeBackendProxy } from "@kie-tools-core/backend/dist/vscode";
import * as KogitoVsCode from "@kie-tools-core/vscode-extension";
import * as vscode from "vscode";
import * as fs from "fs";
import { generateFormCode } from "@kie-tools/form-code-generator/dist/generateFormCode";
import { jbpmBootstrap4FormCodeGeneratorTheme } from "@kie-tools/jbpm-form-code-generator-themes/dist/jbpmBootstrap4FormCodeGeneratorTheme";
import { jbpmPatternflyFormCodeGeneratorTheme } from "@kie-tools/jbpm-form-code-generator-themes/dist/jbpmPatternflyFormCodeGeneratorTheme";

let backendProxy: VsCodeBackendProxy;

export function activate(context: vscode.ExtensionContext) {
  console.info("Extension is alive.");

  const backendI18n = new I18n(backendI18nDefaults, backendI18nDictionaries, vscode.env.language);
  backendProxy = new VsCodeBackendProxy(context, backendI18n);

  KogitoVsCode.startExtension({
    extensionName: "kie-group.bpmn-vscode-extension",
    context: context,
    viewType: "kieKogitoWebviewEditorsBpmn",
    generateSvgCommandId: "extension.kogito.getPreviewSvgBpmn",
    silentlyGenerateSvgCommandId: "extension.kogito.silentlyGenerateSvgBpmn",
    editorEnvelopeLocator: new EditorEnvelopeLocator("vscode", [
      new EnvelopeMapping({
        type: "bpmn",
        filePathGlob: "**/*.bpmn?(2)",
        resourcesPathPrefix: "dist/webview/editors/bpmn",
        envelopeContent: { type: EnvelopeContentType.PATH, path: "dist/webview/BpmnEditorEnvelopeApp.js" },
      }),
    ]),
    backendProxy: backendProxy,
  });

  context.subscriptions.push(
    vscode.commands.registerCommand("extension.kogito.generateForms", async (args: any) => {
      const defaultPath = vscode.workspace.workspaceFolders
        ? vscode.workspace.workspaceFolders[0].uri.fsPath
        : undefined;

      const projectUri = await vscode.window.showOpenDialog({
        canSelectFiles: false,
        canSelectFolders: true,
        canSelectMany: false,
        openLabel: "Select Project Folder",
        defaultUri: defaultPath ? vscode.Uri.file(defaultPath) : undefined,
      });

      // Check if a file was selected
      if (projectUri && projectUri[0]) {
        const projectPath = projectUri[0].fsPath;

        // Show the selected file path
        vscode.window.showInformationMessage(`File path selected: ${projectPath}`);

        // CHECK IF THE PROJECT IS A VALID PROJECT!
        if (fs.existsSync(`${projectPath}/target`) === false) {
          vscode.window.showInformationMessage(`Please, build the project before using the command!`);
          return;
        }
        if (fs.existsSync(`${projectPath}/target/classes/META-INF/jsonSchema`) === false) {
          vscode.window.showInformationMessage(`Couldn't find JSON schemas, did you build using the xyz extension?`);
          return;
        }

        // Here you can perform actions based on the selected options
        const theme = await vscode.window.showQuickPick(["Bootstrap", "Patternfly"], {
          placeHolder: "Select the form style",
        });
        if (theme === undefined) {
          vscode.window.showInformationMessage(`undefined style`);
          return;
        }
        vscode.window.showInformationMessage(`your choice: ${theme}`);

        const ALL_OPTION = "Generate form code all human interactions";
        const SPECIFIC_OPTION = "Generate form code for specific human interactions";
        const humanInteraction = await vscode.window.showQuickPick([ALL_OPTION, SPECIFIC_OPTION], {
          placeHolder: "Select an option",
        });
        vscode.window.showInformationMessage(`your choice: ${humanInteraction}`);

        const jsonSchemaFiles = fs.readdirSync(`${projectPath}/target/classes/META-INF/jsonSchema`);
        let choosenHumanInteractionsJsonSchemas: string[] = [];
        if (humanInteraction === SPECIFIC_OPTION) {
          const options = jsonSchemaFiles.map((jsonSchemaFile) => {
            return { label: jsonSchemaFile.split(".json")[0], picked: false };
          });

          const selectedOptions = await vscode.window.showQuickPick(options, {
            canPickMany: true,
            placeHolder: "Choose the human interactions",
          });

          if (selectedOptions && selectedOptions.length > 0) {
            choosenHumanInteractionsJsonSchemas = selectedOptions.reduce((acc: string[], option) => {
              if (option.picked === true) {
                acc.push(`${option.label}.json`);
              }
              return acc;
            }, []);
            const selectedLabels = selectedOptions.map((option) => `${option.label}`).join(", ");
            vscode.window.showInformationMessage(`Options selected: ${selectedLabels}`);
          } else {
            vscode.window.showErrorMessage("No options selected.");
          }
        } else if (humanInteraction === "Generate for all human interactions") {
          choosenHumanInteractionsJsonSchemas = jsonSchemaFiles;
          vscode.window.showInformationMessage(`generate to all interactions!`);
        }

        const filesThatWillBeOverriden = [];
        for (const humanInteractionJsonSchema of choosenHumanInteractionsJsonSchemas) {
          if (
            fs.existsSync(
              `${projectPath}/src/main/resources/forms/${humanInteractionJsonSchema.split(".json")[0]}.tsx`
            ) ||
            fs.existsSync(
              `${projectPath}/src/main/resources/forms/${humanInteractionJsonSchema.split(".json")[0]}.html`
            )
          ) {
            filesThatWillBeOverriden.push(humanInteractionJsonSchema);
          }
        }

        if (filesThatWillBeOverriden.length === 0) {
          // generate

          // READ PATH FILES;
          const formCode = getFormCode(theme.toLowerCase());

          vscode.window.showInformationMessage(`generated form code!`);
        } else {
          const override = await vscode.window.showQuickPick(["Override", "Cancel"], {
            placeHolder: "Select an option",
          });
          if (override === "Override") {
            // READ PATH FILES;
            const formCode = getFormCode(theme.toLowerCase());
          }
          vscode.window.showInformationMessage(`choice: ${override}`);
        }
      } else {
        vscode.window.showErrorMessage("No file selected.");
      }
    })
  );

  KogitoVsCode.VsCodeRecommendation.showExtendedServicesRecommendation(context);

  console.info("Extension is successfully setup.");
}

function getFormCode(theme: string) {
  if (theme.toLowerCase() === "bootstrap4") {
    return generateFormCode({ formCodeGeneratorTheme: jbpmBootstrap4FormCodeGeneratorTheme, formSchemas: [] });
  }

  if (theme.toLowerCase() === "patternfly") {
    return generateFormCode({ formCodeGeneratorTheme: jbpmPatternflyFormCodeGeneratorTheme, formSchemas: [] });
  }

  return undefined;
}

export function deactivate() {
  backendProxy?.stopServices();
}
