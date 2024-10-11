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
import * as path from "path";
import { generateFormCode } from "@kie-tools/form-code-generator/dist/generateFormCode";
import { jbpmBootstrap4FormCodeGeneratorTheme } from "@kie-tools/jbpm-form-code-generator-themes/dist/jbpmBootstrap4FormCodeGeneratorTheme";
import { jbpmPatternflyFormCodeGeneratorTheme } from "@kie-tools/jbpm-form-code-generator-themes/dist/jbpmPatternflyFormCodeGeneratorTheme";
import { FormSchema } from "@kie-tools/form-code-generator/dist/types";
import { PATTERNFLY_FILE_EXT } from "@kie-tools/form-code-generator-patternfly-theme/dist/theme";
import { BOOTSTRAP4_FILE_EXT } from "@kie-tools/form-code-generator-bootstrap4-theme/dist/theme";

const FORMS_PATH = "src/main/resources/forms";
const JSON_SCHEMA_PATH = "target/classes/META-INF/jsonSchema";

export async function generateFormsCommand() {
  // Get workspace path as default value
  const defaultPath = vscode.workspace.workspaceFolders ? vscode.workspace.workspaceFolders[0].uri.fsPath : undefined;

  // Select project path
  const projectUri = await vscode.window.showOpenDialog({
    canSelectFiles: false,
    canSelectFolders: true,
    canSelectMany: false,
    openLabel: "Select Project Folder",
    defaultUri: defaultPath ? vscode.Uri.file(defaultPath) : undefined,
  });

  // Check if a path was selected
  if (projectUri === undefined || projectUri[0] === undefined) {
    vscode.window.showErrorMessage("You must select a project folder to generate the form code");
    return;
  }

  // Get project path
  const projectPath = projectUri[0].fsPath;

  // Check if project has a target folder
  if (fs.existsSync(`${projectPath}/target`) === false) {
    vscode.window.showInformationMessage(
      `Couldn't find project's "target" folder. Please, install the project before using the command.`
    );
    return;
  }
  // Check if project has the JSON Schemas folder
  if (fs.existsSync(`${projectPath}/${JSON_SCHEMA_PATH}`) === false) {
    vscode.window.showInformationMessage(
      `Couldn't find any JSON Schema, did you install your project ("mvn clean install") using the jbpm dependency?`
    );
    return;
  }

  // Select theme
  const theme = await vscode.window.showQuickPick(["Bootstrap4", "Patternfly"], {
    placeHolder: "Select the form theme",
  });
  // Check if a theme was selected
  if (theme === undefined) {
    vscode.window.showInformationMessage(`Invalid theme. theme: ${theme}`);
    return;
  }

  const formSchemas: FormSchema[] = await getFormSchemas(projectPath);

  const existingFiles: { fileName: string; ext: string }[] = [];
  for (const { name: fileName } of formSchemas) {
    // Check if form `tsx` or `html` file already exists
    if (fs.existsSync(`${projectPath}/${FORMS_PATH}/${path.parse(fileName).name}.${PATTERNFLY_FILE_EXT}`)) {
      existingFiles.push({ fileName, ext: PATTERNFLY_FILE_EXT });
    }
    if (fs.existsSync(`${projectPath}/${FORMS_PATH}/${path.parse(fileName).name}.${BOOTSTRAP4_FILE_EXT}`)) {
      existingFiles.push({ fileName, ext: BOOTSTRAP4_FILE_EXT });
    }
  }

  if (existingFiles.length === 0) {
    saveFormCode(projectPath, theme, formSchemas);
    return;
  }

  const shouldOverride = await vscode.window.showQuickPick(["Override", "Cancel"], {
    placeHolder: "You already have custom forms in this project. Do you want to override them?",
  });
  if (shouldOverride === "Override") {
    // Remove
    existingFiles.forEach(({ fileName, ext }) => {
      fs.rmSync(`${projectPath}/${FORMS_PATH}/${path.parse(fileName).name}.${ext}`);
    });
    saveFormCode(projectPath, theme, formSchemas);
  }
  return;
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

  const jsonSchemaFilesName = fs.readdirSync(`${projectPath}/${JSON_SCHEMA_PATH}`);
  if (humanInteraction === GENERATE_FOR_SPECIFIC_HUMAN_INTERACTIONS) {
    const selectedOptions = await vscode.window.showQuickPick(
      jsonSchemaFilesName.map((jsonSchemaFile) => ({
        label: path.parse(jsonSchemaFile).name,
      })),
      {
        canPickMany: true,
        placeHolder: "Choose the human interactions",
      }
    );

    if (selectedOptions && selectedOptions.length > 0) {
      const jsonSchemaFileNames = selectedOptions.reduce((acc: string[], option) => {
        if (option.label) {
          acc.push(`${option.label}.json`);
        }
        return acc;
      }, []);
      return readAndParseJsonSchemas(projectPath, jsonSchemaFileNames);
    }

    vscode.window.showErrorMessage("No options were selected.");
    return [];
  }

  if (humanInteraction === GENERATE_FOR_ALL_HUMAN_INTERACTIONS) {
    return readAndParseJsonSchemas(projectPath, jsonSchemaFilesName);
  }

  return [];
}

function readAndParseJsonSchemas(projectPath: string, jsonSchemaFilesName: string[]) {
  const formSchemas: FormSchema[] = [];
  const failedParsingFilesName: string[] = [];
  for (const jsonSchemaFileName of jsonSchemaFilesName) {
    try {
      formSchemas.push({
        name: path.parse(jsonSchemaFileName).name, // remove file extension
        schema: JSON.parse(fs.readFileSync(`${projectPath}/${JSON_SCHEMA_PATH}/${jsonSchemaFileName}`, "utf-8")),
      });
    } catch (error) {
      console.error(`Error while parsing ${jsonSchemaFileName}:`, error);
      failedParsingFilesName.push(jsonSchemaFileName);
    }
  }
  if (failedParsingFilesName.length > 0) {
    vscode.window.showErrorMessage(
      "JSON Schema parsing failed for the following files:",
      failedParsingFilesName.join(", ")
    );
  }
  return formSchemas;
}

function saveFormCode(projectPath: string, theme: string, formSchemas: FormSchema[]) {
  const formCode =
    theme.toLowerCase() === "bootstrap4"
      ? generateFormCode({ formCodeGeneratorTheme: jbpmBootstrap4FormCodeGeneratorTheme, formSchemas })
      : theme.toLowerCase() === "patternfly"
        ? generateFormCode({ formCodeGeneratorTheme: jbpmPatternflyFormCodeGeneratorTheme, formSchemas })
        : undefined;

  if (formCode === undefined) {
    vscode.window.showInformationMessage(`It wasn't possible to generate the form code for the theme "${theme}"`);
    return undefined;
  }

  // Save form assets
  const formAssets = formCode.reduce((acc, { formAsset }) => (formAsset !== undefined ? [...acc, formAsset] : acc), []);
  if (formAssets.length > 0) {
    // Create FORMS_PATH directory if doesn't exist
    if (fs.existsSync(`${projectPath}/${FORMS_PATH}`) === false) {
      fs.mkdirSync(`${projectPath}/${FORMS_PATH}`);
    }
    // Create form and config file
    formAssets.forEach((formAsset) => {
      fs.writeFileSync(`${projectPath}/${FORMS_PATH}/${formAsset.assetName}`, formAsset.content);
      fs.writeFileSync(
        `${projectPath}/${FORMS_PATH}/${formAsset.id}.config`,
        JSON.stringify(formAsset.config, null, 2)
      );
    });
    vscode.window.showInformationMessage(
      `Sucess generating form code for the following files: ${formAssets.map((formAsset) => formAsset.assetName).join(", ")}`
    );
  }

  // Show errors
  const formErrors = formCode.reduce(
    (acc, { formError }) => (formError?.error !== undefined ? [...acc, formError] : acc),
    []
  );
  if (formErrors.length > 0) {
    vscode.window.showInformationMessage(
      `Error generating the form for the following files: ${formErrors.map((formError) => formError.fileName).join(", ")}`
    );
  }
}
