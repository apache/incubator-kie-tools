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
import * as fs from "fs";
import { generateFormCode } from "@kie-tools/form-code-generator/dist/generateFormCode";
import { jbpmBootstrap4FormCodeGeneratorTheme } from "@kie-tools/jbpm-form-code-generator-themes/dist/jbpmBootstrap4FormCodeGeneratorTheme";
import { jbpmPatternflyFormCodeGeneratorTheme } from "@kie-tools/jbpm-form-code-generator-themes/dist/jbpmPatternflyFormCodeGeneratorTheme";
import { FormSchema } from "@kie-tools/form-code-generator/dist/types";

export async function generateFormsCommand() {
  const defaultPath = vscode.workspace.workspaceFolders ? vscode.workspace.workspaceFolders[0].uri.fsPath : undefined;

  const projectUri = await vscode.window.showOpenDialog({
    canSelectFiles: false,
    canSelectFolders: true,
    canSelectMany: false,
    openLabel: "Select Project Folder",
    defaultUri: defaultPath ? vscode.Uri.file(defaultPath) : undefined,
  });

  if (projectUri === undefined || projectUri[0] === undefined) {
    vscode.window.showErrorMessage("No file selected.");
    return;
  }

  // Check if a file was selected
  const projectPath = projectUri[0].fsPath;

  // Show the selected file path TOBEREMOVED
  vscode.window.showInformationMessage(`File path selected: ${projectPath}`);

  // CHECK IF THE PROJECT IS A VALID PROJECT!
  if (fs.existsSync(`${projectPath}/target`) === false) {
    vscode.window.showInformationMessage(
      `Couldn't find project's "target" folder. Please, build the project before using the command.`
    );
    return;
  }
  if (fs.existsSync(`${projectPath}/target/classes/META-INF/jsonSchema`) === false) {
    vscode.window.showInformationMessage(
      `Couldn't find any JSON Schema, did you install your project ("mvn clean install") using the jbpm dependency?`
    );
    return;
  }

  // Here you can perform actions based on the selected options
  const theme = await vscode.window.showQuickPick(["Bootstrap4", "Patternfly"], {
    placeHolder: "Select the form theme",
  });
  if (theme === undefined) {
    vscode.window.showInformationMessage(`Invalid theme. theme: ${theme}`);
    return;
  }

  const formSchemas = await getFormSchemas(projectPath);

  const filesThatWillBeOverriden = [];
  for (const formSchema of formSchemas) {
    if (
      fs.existsSync(`${projectPath}/src/main/resources/forms/${formSchema.name.split(".json")[0]}.tsx`) ||
      fs.existsSync(`${projectPath}/src/main/resources/forms/${formSchema.name.split(".json")[0]}.html`)
    ) {
      filesThatWillBeOverriden.push(formSchema);
    }
  }

  if (filesThatWillBeOverriden.length === 0) {
    saveFormCode(projectPath, theme, formSchemas);
  } else {
    const override = await vscode.window.showQuickPick(["Override", "Cancel"], {
      placeHolder: "Select an option",
    });
    if (override === "Override") {
      saveFormCode(projectPath, theme, formSchemas);
    }
    vscode.window.showInformationMessage(`choice: ${override}`);
  }
}

async function getFormSchemas(projectPath: string): Promise<FormSchema[]> {
  const GENERATE_FOR_ALL_HUMAN_INTERACTIONS = "Generate form code all human interactions";
  const GENERATE_FOR_SPECIFIC_HUMAN_INTERACTIONS = "Generate form code for specific human interactions";
  const humanInteraction = await vscode.window.showQuickPick(
    [GENERATE_FOR_ALL_HUMAN_INTERACTIONS, GENERATE_FOR_SPECIFIC_HUMAN_INTERACTIONS],
    {
      placeHolder: "Select an option",
    }
  );

  const jsonSchemaFiles = fs.readdirSync(`${projectPath}/target/classes/META-INF/jsonSchema`);
  if (humanInteraction === GENERATE_FOR_SPECIFIC_HUMAN_INTERACTIONS) {
    const humanInteractionOptions = jsonSchemaFiles.map((jsonSchemaFile) => ({
      label: jsonSchemaFile.split(".json")[0],
      picked: false,
    }));

    const selectedOptions = await vscode.window.showQuickPick(humanInteractionOptions, {
      canPickMany: true,
      placeHolder: "Choose the human interactions",
    });

    if (selectedOptions && selectedOptions.length > 0) {
      const jsonSchemaFiles = selectedOptions.reduce((acc: string[], option) => {
        if (option.picked === true) {
          acc.push(`${option.label}.json`);
        }
        return acc;
      }, []);

      return jsonSchemaFiles.reduce((acc, jsonSchemaFile) => {
        acc.push({
          name: jsonSchemaFile,
          schema: fs.readFileSync(`${projectPath}/target/classes/META-INF/jsonSchema/${jsonSchemaFile}`, "utf-8"),
        });
        return acc;
      }, [] as FormSchema[]);
    }

    vscode.window.showErrorMessage("No options were selected.");
    return [];
  }

  if (humanInteraction === GENERATE_FOR_ALL_HUMAN_INTERACTIONS) {
    return jsonSchemaFiles.reduce((acc, jsonSchemaFile) => {
      acc.push({
        name: jsonSchemaFile,
        schema: fs.readFileSync(`${projectPath}/target/classes/META-INF/jsonSchema/${jsonSchemaFile}`, "utf-8"),
      });
      return acc;
    }, [] as FormSchema[]);
  }

  return [];
}

function saveFormCode(projectPath: string, theme: string, formSchemas: FormSchema[]) {
  const formCode =
    theme.toLowerCase() === "bootstrap4"
      ? generateFormCode({ formCodeGeneratorTheme: jbpmBootstrap4FormCodeGeneratorTheme, formSchemas })
      : theme.toLowerCase() === "patternfly"
        ? generateFormCode({ formCodeGeneratorTheme: jbpmPatternflyFormCodeGeneratorTheme, formSchemas })
        : undefined;

  if (formCode === undefined) {
    vscode.window.showInformationMessage(`Invalid theme. theme: ${theme}`);
    return undefined;
  }

  console.log(JSON.stringify(formCode));

  // Save form assets
  const formAssets = formCode.reduce((acc, { formAsset }) => (formAsset !== undefined ? [...acc, formAsset] : acc), []);
  if (formAssets.length > 0) {
    if (fs.existsSync(`${projectPath}/src/main/resources/forms`) === false) {
      fs.mkdirSync(`${projectPath}/src/main/resources/forms`);
    }
    formAssets.forEach((formAsset) => {
      fs.writeFileSync(`${projectPath}/src/main/resources/forms/${formAsset.assetName}`, formAsset.content);
    });
    vscode.window.showInformationMessage(
      `Sucess generating form code for the following files: ${formAssets.map((formAsset) => formAsset.assetName).join(", ")}`
    );
  }

  // Show errors
  const formErrors = formCode.reduce((acc, { formError }) => (formError !== undefined ? [...acc, formError] : acc), []);
  if (formErrors.length > 0) {
    console.log(JSON.stringify(formErrors));
    vscode.window.showInformationMessage(
      `Error generating the form for the following files: ${formErrors.map((formError) => formError.fileName).join(", ")}`
    );
  }
}
