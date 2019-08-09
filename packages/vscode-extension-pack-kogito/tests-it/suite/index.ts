/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import * as path from "path";
import * as Mocha from "mocha";
import * as glob from "glob";

export function run(testsRoot: string, callback: (error: any, failures?: number) => void): void {
  const mocha = new Mocha({
    ui: "tdd",
    useColors: true,
    timeout: 10000
  });

  glob("**/**.test.js", { cwd: testsRoot }, (err, files) => {
    if (err) {
      return callback(err);
    }

    files.forEach(f => mocha.addFile(path.resolve(testsRoot, f)));

    try {
      mocha.run(failures => callback(null, failures));
    } catch (err) {
      callback(err);
    }
  });
}
