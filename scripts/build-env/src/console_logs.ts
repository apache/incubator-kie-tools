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

import { ERROR_ACCESS_LOG_FILE_ABSOLUTE_PATH_ENV_VAR_NAME } from "./lib";

export const LOGS = {
  error: {
    foundConflictingVars() {
      return `[build-env] ERROR: Found conflicting vars.`;
    },
    foundConflictingRootProps() {
      return `[build-env] ERROR: Found conflicting root properties.`;
    },
    envNotFound(args: { envPath: string }) {
      return `[build-env] ERROR: Env not found at '${args.envPath}'`;
    },
    envFileLoadingError(args: { envFilePath: string }) {
      return `[build-env] ERROR: Error loading env at '${args.envFilePath}'`;
    },
    findEnvRecursionStopped(args: { startDir: string; curDir: string; envRecursionStopPath: string }) {
      return `[build-env] ERROR: Couldn't load env from '${args.startDir}' to '${args.curDir}'. Stopped at '${args.envRecursionStopPath}'`;
    },
    cantNegateNonBoolean(args: { envPropertyValue: string | boolean | number }) {
      return `[build-env] ERROR: Cannot negate non-boolean value '${args.envPropertyValue}'`;
    },
    cantReturnNonString(args: { propertyPath: string; propertyType: string }) {
      return `[build-env] ERROR: Env property '${args.propertyPath}' is not of type "string", "number", or "boolean". Found "${args.propertyType}":`;
    },
    pleaseProvideEnvPropertyPath() {
      return `[build-env] ERROR: Please provide an env property path.`;
    },
    propertyNotFound(args: { propertyPath: string }): string {
      return `[build-env] ERROR: Env property '${args.propertyPath}' not found.`;
    },
    usingConfiguredAccessErrorLogFile({ logFilePath }: { logFilePath: string }) {
      return `[build-env] ERROR: Env property access error. Using configured access errors log file '${logFilePath}'`;
    },
    errorWritingAccessErrorLog({ logFilePath }: { logFilePath: string }) {
      return `[build-env] ERROR: Error writing access error log to '${logFilePath}'. Property access errors won't be detectable.`;
    },
  },
  warn: {
    defaultingAccessErrorsLogFileToTmpDirBecauseNotAbsolute(args: { suppliedPath: string; logFilePath: string }) {
      return `[build-env] WARNING: Supplied path for access errors log file is not absolute ('${args.suppliedPath}'). Defaulting to '${args.logFilePath}'.`;
    },
    defaultingAccessErrorsLogFileToTmpDirBecauseNotSupplied(args: { logFilePath: string }) {
      return `[build-env] WARNING: Access errors log file absolute path not supplied via env var ${ERROR_ACCESS_LOG_FILE_ABSOLUTE_PATH_ENV_VAR_NAME}. Defaulting to '${args.logFilePath}'.`;
    },
  },
  info: {
    seeAllEnvProperties() {
      return `[build-env] See all env properties with 'build-env --print-env-json'`;
    },
    wroteAccessErrorLog(args: { logFilePath: string }) {
      return `[build-env] Wrote access error to: '${args.logFilePath}'.`;
    },
  },
  debug: {
    envFound(args: { envPath: string }) {
      return `[build-env] DEBUG: Found env at '${args.envPath}'`;
    },
  },
};
