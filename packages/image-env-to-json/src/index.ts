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

import { createCommand } from "commander";
import { existsSync, readFileSync, writeFileSync } from "fs";
import { join } from "path";
import { version as packageVersion } from "../package.json";

const ENV_JSON_FILE = "env.json";

function main() {
  const program = createCommand();

  program.name("image-env-to-json").version(packageVersion);
  program.showHelpAfterError("(add --help for additional information)");
  program
    .requiredOption("-d, --directory <directory>", `directory to create or update an existing ${ENV_JSON_FILE} file`)
    .requiredOption("-n, --names <names...>", "environment variable names to look for")
    .parse();

  const options = program.opts();

  console.info(`Looking for environment variables: ${options.names.join(", ")}`);

  if (!existsSync(options.directory)) {
    console.error(`Directory '${options.directory}' does not exist. Please provide an existing directory.`);
    process.exit(1);
  }

  const envJsonPath = join(options.directory, ENV_JSON_FILE);
  if (!existsSync(envJsonPath)) {
    console.info(`Creating ${ENV_JSON_FILE} file in '${options.directory}'`);
    writeFileSync(envJsonPath, JSON.stringify({}));
  }

  const envJson = JSON.parse(readFileSync(envJsonPath).toString());

  let isUpdated = false;

  for (const name of options.names) {
    const value = process.env[name];
    if (value !== undefined) {
      isUpdated = true;
      console.info(`Setting environment variable '${name}' with value '${value}'`);
      envJson[name] = value;
    }
  }

  writeFileSync(envJsonPath, JSON.stringify(envJson, null, 2));

  if (isUpdated) {
    console.info(`${ENV_JSON_FILE} file has been updated in '${options.directory}'`);
  } else {
    console.info(`No environment variables have been updated in '${options.directory}'`);
  }
}

main();
