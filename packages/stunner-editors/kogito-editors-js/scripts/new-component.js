/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

const prompts = require("prompts");
const path = require("path");
const fse = require("fs-extra");

const greenLog = (message) => console.log("\x1b[32m%s\x1b[0m", message);

const redLog = (message) => console.log("\x1b[31m%s\x1b[0m", message);

const handleError = () => {
  redLog(`Error during creation of the '${componentName}' component.`);
  throw error;
};

const copyTemplateDirectory = (componentName) => {
  const sourceDir = path.resolve(__dirname, "../packages/.template-component");
  const targetDir = path.resolve(__dirname, `../packages/${componentName}`);

  try {
    fse.copySync(sourceDir, targetDir);
  } catch (error) {
    handleError(error);
  }
};

const renameTemplateComponent = (componentName) => {
  const replaceComponentName = (file) => {
    const fileName = path.resolve(__dirname, `../packages/${componentName}/${file}`);

    fse.readFile(fileName, "utf8", (error, data) => {
      if (error) {
        handleError(error);
      }
      const result = data.replace(/___COMPONENT_NAME___/g, componentName);
      fse.writeFile(fileName, result, "utf8", function (error) {
        if (error) {
          handleError(error);
        }
      });
    });
  };

  replaceComponentName("package.json");
  replaceComponentName("webpack.development.js");
  replaceComponentName("webpack.production.js");
  replaceComponentName("showcase/package.json");
};

(async () => {
  const response = await prompts({
    type: "text",
    name: "component",
    message: "Type the name of your new component:",
    validate: (component) => {
      if (component.match(/^[a-z0-9\-]+$/i)) {
        return true;
      } else {
        return `The name of your project may contain only hyphens, letters, or numbers.`;
      }
    },
  });

  const componentName = response["component"].endsWith("-component")
    ? response["component"]
    : `${response["component"]}-component`;

  copyTemplateDirectory(componentName);
  renameTemplateComponent(componentName);

  greenLog(`The '${componentName}' component has been successfully created!`);
})();
