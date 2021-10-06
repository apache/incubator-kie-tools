/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import * as cp from "child_process";
import * as os from "os";
import { lte } from "semver";

interface Version {
  major: number;
  minor: number;
  patch: number;
}

/**
 * Verify if Maven is installed on the local machine.
 * @param version Optional minimum version to match.
 * @returns Whether Maven is installed or not.
 */
export function isMavenAvailable(version?: Version): Promise<boolean> {
  return new Promise((resolve) => {
    cp.exec("mvn -version", (error, stdout, _) => {
      if (error) {
        resolve(false);
        return;
      }

      const firstLine = stdout.toString().split("\n")[0];
      const regexMatch = new RegExp("^(Apache Maven) (\\d+)(?:\\.(\\d+)(?:\\.(\\d+)(?:[_\\.](\\d+))?)?)?").exec(
        firstLine
      );

      if (!regexMatch) {
        resolve(false);
        return;
      }

      if (!version) {
        resolve(!!regexMatch);
        return;
      }

      const [, , major, minor, patch] = regexMatch;

      const requiredVersion = Object.values(version).join(".");
      const actualVersion = `${+major}.${minor ? +minor : 0}.${patch ? +patch : 0}`;

      resolve(lte(requiredVersion, actualVersion));
    });
  });
}

/**
 * Verify if Java is installed on the local machine.
 * @param version Optional minimum version to match.
 * @returns Whether Java is installed or not.
 */
export function isJavaAvailable(version?: Version): Promise<boolean> {
  return new Promise((resolve) => {
    cp.exec("java -version", (error, _, stderr) => {
      if (error) {
        resolve(false);
        return;
      }

      const firstLine = stderr.toString().split("\n")[0];
      const regexMatch = new RegExp(
        '^(java|openjdk) (version) "?(\\d+)(?:\\.(\\d+)(?:\\.(\\d+)(?:[_\\.](\\d+))?)?)?"?'
      ).exec(firstLine);

      if (!regexMatch) {
        resolve(false);
        return;
      }

      if (!version) {
        resolve(!!regexMatch);
        return;
      }

      const [, , , major, minor, patch] = regexMatch;

      const requiredVersion = Object.values(version).join(".");
      const actualVersion = `${+major}.${minor ? +minor : 0}.${patch ? +patch : 0}`;

      resolve(lte(requiredVersion, actualVersion));
    });
  });
}

/**
 * Kill the given process.
 * @param process Process to be killed.
 */
export function killProcess(process: cp.ChildProcess) {
  switch (os.platform()) {
    case "win32":
      cp.spawn("taskkill", ["/pid", process.pid!.toString(), "/f", "/t"]);
      break;
    case "darwin":
    case "linux":
    default:
      process.kill("SIGINT");
      break;
  }
}
