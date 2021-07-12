/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import inquirer, { Answers } from "inquirer";

import { Args, generateForms } from "../generation";
import { checkKogitoProjectHasForms, checkKogitoProjectStructure } from "../generation/fs";

export function run() {
  const validateProjectPath = (path: string): string | boolean => {
    if (!path || path === "") {
      return "Please type a Kogito Project path";
    }
    try {
      checkKogitoProjectStructure(path);
    } catch (err) {
      return err.message;
    }

    return true;
  };

  const isOverwriteVisible = (answers: Answers): boolean => {
    return checkKogitoProjectHasForms(answers.path);
  };

  const execute = (answers: Answers): void => {
    const args: Args = {
      path: answers.path,
      type: answers.type,
      overwrite: answers.overwrite,
    };

    const message =
      "\nCurrent selection:" +
      `\nProject path: ${args.path}` +
      `\nForm type: ${args.type}` +
      `${args.overwrite !== undefined ? `\nOverwrite existing forms: ${args.overwrite}` : ""}\n`;

    console.log(message);

    inquirer
      .prompt({
        name: "confirm",
        type: "confirm",
        message: "Do you want to continue?",
        default: true,
      })
      .then((answers) => {
        if (answers.confirm) {
          generateForms(args);
        }
        console.log("\nGood bye!");
      });
  };

  const questions = [
    {
      name: "path",
      type: "string",
      message: "Type your Kogito Project path:",
      validate: validateProjectPath,
    },
    {
      name: "overwrite",
      type: "confirm",
      message: "The project already contains forms, do you want to overwrite the existing ones?",
      default: false,
      when: isOverwriteVisible,
    },
    {
      name: "type",
      type: "list",
      message: "Select the Form type:",
      choices: ["patternfly", "bootstrap"],
      default: "patternfly",
    },
  ];

  console.log("Kogito Form Generation CLI");
  console.log("===========================");
  console.log();
  console.log("This tool will help you generate forms for User Tasks in your Kogito Projects.");
  console.log(
    "The tool will search for the User Tasks JSON schemas generated in your project, so make sure the project is build."
  );
  console.log("The generated forms will be stored as resources in your project (in src/main/resources/forms folder).");
  console.log();

  inquirer.prompt(questions).then(execute);
}
