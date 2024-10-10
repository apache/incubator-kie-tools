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

const FORMS_PATH = "src/main/resources/forms";
const JSON_SCHEMA_PATH = "target/classes/META-INF/jsonSchema";

export async function generateFormsCommand() {
  // Use the workspace path as default value
  const defaultPath = vscode.workspace.workspaceFolders ? vscode.workspace.workspaceFolders[0].uri.fsPath : undefined;

  const projectUri = await vscode.window.showOpenDialog({
    canSelectFiles: false,
    canSelectFolders: true,
    canSelectMany: false,
    openLabel: "Select Project Folder",
    defaultUri: defaultPath ? vscode.Uri.file(defaultPath) : undefined,
  });

  if (projectUri === undefined || projectUri[0] === undefined) {
    vscode.window.showErrorMessage("You must select a project folder to generate the form code");
    return;
  }

  // Get project path
  const projectPath = projectUri[0].fsPath;

  // Check if is a valid project
  if (fs.existsSync(`${projectPath}/target`) === false) {
    vscode.window.showInformationMessage(
      `Couldn't find project's "target" folder. Please, install the project before using the command.`
    );
    return;
  }
  if (fs.existsSync(`${projectPath}/${JSON_SCHEMA_PATH}`) === false) {
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
      fs.existsSync(`${projectPath}/${FORMS_PATH}/${formSchema.name.split(".json")[0]}.tsx`) ||
      fs.existsSync(`${projectPath}/${FORMS_PATH}/${formSchema.name.split(".json")[0]}.html`)
    ) {
      filesThatWillBeOverriden.push(formSchema);
    }
  }

  console.log("FORM SCHEMAS", formSchemas);
  console.log("STRINGIFYIED", JSON.stringify(formSchemas));
  if (filesThatWillBeOverriden.length === 0) {
    saveFormCode(projectPath, theme, formSchemas);
  } else {
    const override = await vscode.window.showQuickPick(["Override", "Cancel"], {
      placeHolder: "Select an option",
    });
    if (override === "Override") {
      saveFormCode(projectPath, theme, formSchemas);
    }
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

  const jsonSchemaFilesName = fs.readdirSync(`${projectPath}/${JSON_SCHEMA_PATH}`);
  if (humanInteraction === GENERATE_FOR_SPECIFIC_HUMAN_INTERACTIONS) {
    const humanInteractionOptions = jsonSchemaFilesName.map((jsonSchemaFile) => ({
      label: jsonSchemaFile.split(".json")[0],
      picked: false,
    }));

    const selectedOptions = await vscode.window.showQuickPick(humanInteractionOptions, {
      canPickMany: true,
      placeHolder: "Choose the human interactions",
    });

    if (selectedOptions && selectedOptions.length > 0) {
      const jsonSchemaFileNames = selectedOptions.reduce((acc: string[], option) => {
        if (option.picked === true) {
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
        name: jsonSchemaFileName.split(".json")[0], // remove file extension
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
    vscode.window.showInformationMessage(`Invalid theme. theme: ${theme}`);
    return undefined;
  }

  // Save form assets
  const formAssets = formCode.reduce((acc, { formAsset }) => (formAsset !== undefined ? [...acc, formAsset] : acc), []);
  if (formAssets.length > 0) {
    if (fs.existsSync(`${projectPath}/${FORMS_PATH}`) === false) {
      fs.mkdirSync(`${projectPath}/${FORMS_PATH}`);
    }
    formAssets.forEach((formAsset) => {
      fs.writeFileSync(`${projectPath}/${FORMS_PATH}/${formAsset.assetName}`, formAsset.content);
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
    console.log(JSON.stringify(formErrors));
    vscode.window.showInformationMessage(
      `Error generating the form for the following files: ${formErrors.map((formError) => formError.fileName).join(", ")}`
    );
  }
}
