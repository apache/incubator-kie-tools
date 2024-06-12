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

import { ERROR_ACCESS_LOG_FILE_ABSOLUTE_PATH_ENV_VAR_NAME, findEnv } from "./lib";
import { LOGS } from "./console_logs";

import * as path from "path";
import * as fs from "fs";
import * as os from "os";

import { treatSpecialPrintCases } from "./special_print_cases";

const opt = process.argv[2];
const flag = process.argv[3];

async function main() {
  const { env, vars, self } = await findEnv(path.resolve("."), path.resolve("."));

  // This will exit the process if a special print case is requested using `opt`.
  treatSpecialPrintCases({ opt, vars, self });

  const propertyPath = opt;
  if (!propertyPath) {
    console.error(LOGS.error.pleaseProvideEnvPropertyPath());
    console.error(LOGS.info.seeAllEnvProperties());
    throw new Error(`[build-env] property-path-not-provided-error`);
  }

  let envPropertyValue: any = env;
  for (const p of propertyPath.split(".")) {
    envPropertyValue = envPropertyValue[p];
    if (envPropertyValue === undefined) {
      console.error(LOGS.error.propertyNotFound({ propertyPath }));
      console.error(LOGS.info.seeAllEnvProperties());
      throw new Error(`[build-env] property-not-found-error`);
    }
  }

  if (
    typeof envPropertyValue !== "string" &&
    typeof envPropertyValue !== "boolean" &&
    typeof envPropertyValue !== "number"
  ) {
    console.error(LOGS.error.cantReturnNonString({ propertyPath, propertyType: typeof envPropertyValue }));
    console.error(envPropertyValue);
    throw new Error(`[build-env] cant-return-non-string-error`);
  }

  if (flag === "--not") {
    const isBoolean = `${envPropertyValue}` === "true" || `${envPropertyValue}` === "false";
    if (!isBoolean) {
      console.error(LOGS.error.cantNegateNonBoolean({ envPropertyValue }));
      throw new Error(`[build-env] cant-negate-non-boolean-error`);
    }

    console.log(!(`${envPropertyValue}` === "true"));
  } else {
    console.log(envPropertyValue);
  }
}

main().catch((e) => {
  const suppliedPath = process.env[ERROR_ACCESS_LOG_FILE_ABSOLUTE_PATH_ENV_VAR_NAME];
  const defaultPath = path.join(os.tmpdir(), "build-env-access-errors.log");

  let logFilePath;
  if (!suppliedPath) {
    logFilePath = defaultPath;
    console.error(LOGS.warn.defaultingAccessErrorsLogFileToTmpDirBecauseNotSupplied({ logFilePath }));
  } else if (!path.isAbsolute(suppliedPath)) {
    logFilePath = defaultPath;
    console.error(LOGS.warn.defaultingAccessErrorsLogFileToTmpDirBecauseNotSupplied({ logFilePath }));
  } else {
    logFilePath = suppliedPath;
    console.error(LOGS.error.usingConfiguredAccessErrorLogFile({ logFilePath }));
  }

  try {
    fs.appendFileSync(logFilePath, `${e.message} :: cwd:${path.resolve(".")} opt:${opt} flag:${flag}\n`, "utf-8");
    console.error(LOGS.info.wroteAccessErrorLog({ logFilePath }));
  } catch (e) {
    console.error(LOGS.error.errorWritingAccessErrorLog({ logFilePath }));
    console.error(e);
    throw e;
  }
});
