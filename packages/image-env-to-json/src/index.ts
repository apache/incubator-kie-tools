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
import { existsSync, readFile, readFileSync, writeFileSync } from "fs";
import { join } from "path";
import { version as packageVersion } from "../package.json";
import { Schema, validate } from "jsonschema";

const ENV_JSON_FILE = "env.json";

function getEnvVarValueAsJson(name: string) {
  const envVarStringValue = process.env[name];
  if (!envVarStringValue) {
    return undefined;
  }

  try {
    const envVarJsonValue = JSON.parse(envVarStringValue);
    console.info(`[image-env-to-json] '${name}' is a valid JSON. Using parsed JSON from '${name}'.`);
    return envVarJsonValue;
  } catch (e) {
    console.info(`[image-env-to-json] '${name}' is not a valid JSON. Using '${name}' as string.`);
    return envVarStringValue;
  }
}

function main() {
  const program = createCommand();

  program.name("image-env-to-json").version(packageVersion);
  program.showHelpAfterError("(add --help for additional information)");
  program
    .requiredOption("-d, --directory <directory>", `directory to create or update an existing '${ENV_JSON_FILE}' file.`)
    .option("-n, --names <names...>", "Environment variable names to look for. Using a JSON Schema is preferred.")
    .option("--json-schema <path>", `JSON Schema file to validate '${ENV_JSON_FILE}'`)
    .parse();

  const options = program.opts();

  if (!existsSync(options.directory)) {
    console.error(
      `[image-env-to-json] Directory '${options.directory}' does not exist. Please provide an existing directory.`
    );
    process.exit(1);
  }

  const envJsonPath = join(options.directory, ENV_JSON_FILE);
  if (!existsSync(envJsonPath)) {
    console.info(`[image-env-to-json] Creating '${ENV_JSON_FILE}' file in '${options.directory}'`);
    writeFileSync(envJsonPath, JSON.stringify({}));
  }

  if (options.jsonSchema && options.names) {
    console.error(`[image-env-to-json] Can't use '-n, --names' together with '--json-schema'`);
    process.exit(1);
  }
  if (!options.jsonSchema && !options.names) {
    console.error(`[image-env-to-json] One of '-n, --names' or '--json-schema' must be set.`);
    process.exit(1);
  }

  let schema: Schema | undefined;
  let envVarNames = [];
  if (options.jsonSchema) {
    console.info(`[image-env-to-json] Reading JSON Schema from '${options.jsonSchema}'...`);
    try {
      schema = JSON.parse(readFileSync(options.jsonSchema, "utf-8"));
      envVarNames = [...Object.keys(schema?.definitions?.[schema.$id ?? 0].properties ?? {})];
    } catch (e) {
      console.error(`[image-env-to-json] Error parsing JSON Schema from '${options.jsonSchema}'`, e);
      process.exit(1);
    }
  } else if (options.names) {
    console.info(`[image-env-to-json] No JSON Schema provided. Validation will not happen.`);
    schema = undefined;
    envVarNames = options.names;
    process.exit(0);
  } else {
    throw new Error("Impossible scenario.");
  }

  // Reading original env.json from the image.
  const envJson = JSON.parse(readFileSync(envJsonPath).toString());

  console.info(`[image-env-to-json] Looking for environment variables: ${envVarNames.join(", ")}...`);
  let isUpdated = false;
  for (const name of envVarNames) {
    const value = getEnvVarValueAsJson(name);
    if (value !== undefined) {
      isUpdated = true;
      console.info(`[image-env-to-json] Setting environment variable '${name}' with value '${JSON.stringify(value)}'`);
      envJson[name] = value;
    }
  }

  writeFileSync(envJsonPath, JSON.stringify(envJson, null, 2));

  if (isUpdated) {
    console.info(
      `[image-env-to-json] '${ENV_JSON_FILE}' file has been updated in '${options.directory}' according to environment variables.`
    );
  } else {
    console.info(`[image-env-to-json] Original '${options.directory}' used. No environment variables overwrites.`);
  }

  if (schema) {
    const validation = validate(envJson, schema);
    if (!validation.valid) {
      console.error(`[image-env-to-json] Invalid '${ENV_JSON_FILE}' at '${envJsonPath}'`, ...validation.errors);
      process.exit(1);
    } else {
      console.info(`[image-env-to-json] '${ENV_JSON_FILE}' at '${envJsonPath}' is valid.`);
      console.info(`[image-env-to-json] Done.`);
    }
  } else {
    console.info(`[image-env-to-json] Done.`);
  }
}

main();
