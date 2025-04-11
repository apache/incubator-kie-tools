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
import { removeInvalidVarChars } from "@kie-tools/jbpm-form-code-generator-themes/dist/removeInvalidVarChars";
import { jbpmBootstrap4FormCodeGeneratorTheme } from "@kie-tools/jbpm-form-code-generator-themes/dist/jbpmBootstrap4FormCodeGeneratorTheme";
import { jbpmPatternflyFormCodeGeneratorTheme } from "@kie-tools/jbpm-form-code-generator-themes/dist/jbpmPatternflyFormCodeGeneratorTheme";
import { FormSchema } from "@kie-tools/form-code-generator/dist/types";
import { PATTERNFLY_FILE_EXT } from "@kie-tools/form-code-generator-patternfly-theme/dist/theme";
import { BOOTSTRAP4_FILE_EXT } from "@kie-tools/form-code-generator-bootstrap4-theme/dist/theme";

const FORM_CODE_GENERATION_DEST_PATH = "src/main/resources/custom-forms-dev";
const JSON_SCHEMA_PATH = "target/classes/META-INF/jsonSchema";

const BOOTSTRAP_UI_LIBRARY_NAME = "Bootstrap 4";
const PATTERNFLY_UI_LIBRARY_NAME = "PatternFly";

export async function generateFormsCommand() {
  // Get workspace path as default value
  const defaultPath = vscode.workspace.workspaceFolders ? vscode.workspace.workspaceFolders[0].uri.fsPath : undefined;

  // Select project path
  const projectUri = await vscode.window.showOpenDialog({
    canSelectFiles: false,
    canSelectFolders: true,
    canSelectMany: false,
    title: "Select project folder",
    defaultUri: defaultPath ? vscode.Uri.file(defaultPath) : undefined,
  });

  // Check if a path was selected
  if (projectUri === undefined || projectUri[0] === undefined) {
    return;
  }

  // Get project path
  const projectPath = projectUri[0].fsPath;

  // Check if project has a target folder
  if (fs.existsSync(`${projectPath}/target`) === false) {
    vscode.window.showErrorMessage(
      `Couldn't find project's "target" folder. Please install your project before using this command.`
    );
    return;
  }
  // Check if project has the JSON Schemas folder
  if (fs.existsSync(`${projectPath}/${JSON_SCHEMA_PATH}`) === false) {
    vscode.window.showErrorMessage(
      `Couldn't find any JSON Schema, did you install your project ("mvn clean install")?`
    );
    return;
  }

  // Select UI library
  const uiLibrary = await vscode.window.showQuickPick([BOOTSTRAP_UI_LIBRARY_NAME, PATTERNFLY_UI_LIBRARY_NAME], {
    placeHolder: "Select the UI library for the generated form(s)",
  });

  // Check if a UI library was selected
  if (uiLibrary === undefined) {
    return;
  }

  const formSchemas: FormSchema[] = await getFormSchemas(projectPath);

  const existingFiles: { fileName: string; ext: string }[] = [];
  for (const { name: fileName } of formSchemas) {
    // Check if form `tsx` or `html` file already exists
    if (
      fs.existsSync(
        `${projectPath}/${FORM_CODE_GENERATION_DEST_PATH}/${removeInvalidVarChars(path.parse(fileName).name)}.${PATTERNFLY_FILE_EXT}`
      )
    ) {
      existingFiles.push({ fileName, ext: PATTERNFLY_FILE_EXT });
    }
    if (
      fs.existsSync(
        `${projectPath}/${FORM_CODE_GENERATION_DEST_PATH}/${removeInvalidVarChars(path.parse(fileName).name)}.${BOOTSTRAP4_FILE_EXT}`
      )
    ) {
      existingFiles.push({ fileName, ext: BOOTSTRAP4_FILE_EXT });
    }
  }

  if (existingFiles.length === 0) {
    saveFormCode(projectPath, uiLibrary, formSchemas);
    return;
  }

  const shouldOverride = await vscode.window.showQuickPick(["Override", "Cancel"], {
    placeHolder: "You already have custom forms in this project. Do you want to override them?",
  });
  if (shouldOverride === "Override") {
    // Remove previous files.
    // In case the user has `tsx` files and are generating `html` files, the `tsx` files should be removed
    existingFiles.forEach(({ fileName, ext }) => {
      fs.rmSync(
        `${projectPath}/${FORM_CODE_GENERATION_DEST_PATH}/${removeInvalidVarChars(path.parse(fileName).name)}.${ext}`
      );
    });
    saveFormCode(projectPath, uiLibrary, formSchemas);
  }
  return;
}

async function getFormSchemas(projectPath: string): Promise<FormSchema[]> {
  const GENERATE_FOR_ALL_HUMAN_INTERACTIONS = "Generate form code for all User Tasks";
  const GENERATE_FOR_SPECIFIC_HUMAN_INTERACTIONS = "Generate form code for specific User Tasks";

  const generationChoice = await vscode.window.showQuickPick(
    [GENERATE_FOR_ALL_HUMAN_INTERACTIONS, GENERATE_FOR_SPECIFIC_HUMAN_INTERACTIONS],
    {
      placeHolder: "Select an option",
    }
  );

  const jsonSchemaFilesName = fs.readdirSync(`${projectPath}/${JSON_SCHEMA_PATH}`);
  if (generationChoice === GENERATE_FOR_SPECIFIC_HUMAN_INTERACTIONS) {
    const selectedOptions = await vscode.window.showQuickPick(
      jsonSchemaFilesName.map((jsonSchemaFile) => ({
        label: path.parse(jsonSchemaFile).name,
      })),
      {
        canPickMany: true,
        placeHolder: "Choose the User Tasks",
      }
    );

    if (selectedOptions === undefined || selectedOptions.length === 0) {
      return [];
    }

    return readAndParseJsonSchemas(
      projectPath,
      selectedOptions.reduce(
        (jsonSchemaFilesName: string[], option) =>
          option.label ? [...jsonSchemaFilesName, `${option.label}.json`] : jsonSchemaFilesName,
        []
      )
    );
  }

  if (generationChoice === GENERATE_FOR_ALL_HUMAN_INTERACTIONS) {
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
        name: path.parse(jsonSchemaFileName).name,
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

function saveFormCode(projectPath: string, uiLibrary: string, formSchemas: FormSchema[]) {
  const formCode =
    uiLibrary.toLowerCase() === BOOTSTRAP_UI_LIBRARY_NAME.toLowerCase()
      ? generateFormCode({ formCodeGeneratorTheme: jbpmBootstrap4FormCodeGeneratorTheme, formSchemas })
      : uiLibrary.toLowerCase() === PATTERNFLY_UI_LIBRARY_NAME.toLowerCase()
        ? generateFormCode({ formCodeGeneratorTheme: jbpmPatternflyFormCodeGeneratorTheme, formSchemas })
        : undefined;

  if (formCode === undefined) {
    vscode.window.showErrorMessage(`The "${uiLibrary}" UI library isn't available.`);
    return undefined;
  }

  // Save form assets
  const formAssets = formCode.reduce((acc, { formAsset }) => (formAsset !== undefined ? [...acc, formAsset] : acc), []);
  if (formAssets.length > 0) {
    // Create FORMS_PATH directory if doesn't exist
    if (fs.existsSync(`${projectPath}/${FORM_CODE_GENERATION_DEST_PATH}`) === false) {
      fs.mkdirSync(`${projectPath}/${FORM_CODE_GENERATION_DEST_PATH}`);
    }
    // Create form code and config files
    formAssets.forEach((formAsset) => {
      fs.writeFileSync(
        `${projectPath}/${FORM_CODE_GENERATION_DEST_PATH}/${formAsset.fileNameWithoutInvalidVarChars}`,
        formAsset.content
      );
      fs.writeFileSync(
        `${projectPath}/${FORM_CODE_GENERATION_DEST_PATH}/${formAsset.nameWithoutInvalidTsVarChars}.config`,
        JSON.stringify(formAsset.config, null, 2)
      );
    });
    vscode.window.showInformationMessage(
      `Success generating form code for the following files: ${formAssets.map((formAsset) => formAsset.fileName).join(", ")}`
    );
  }

  // Show errors
  const formErrors = formCode.reduce(
    (acc, { formError }) => (formError?.error !== undefined ? [...acc, formError] : acc),
    []
  );
  if (formErrors.length > 0) {
    vscode.window.showErrorMessage(
      `Error generating form code for the following files: ${formErrors.map((formError) => formError.fileName).join(", ")}`
    );
  }
}
