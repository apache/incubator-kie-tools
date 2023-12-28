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
import { Notification } from "@kie-tools-core/notifications/dist/api";
import { ValidationResult } from "./ValidationResult";

export function validationPromise(yaml: String): Promise<Notification[]> {
  return new Promise((resolve, reject) => {
    const worker = new Worker("/yard-validator-worker.js");
    worker.postMessage(yaml);
    worker.onmessage = (e) => {
      const notifications: Notification[] = [];

      try {
        const validationResult: ValidationResult = JSON.parse(e.data);

        for (const result of validationResult.result) {
          for (const location of result.locations) {
            notifications.push({
              normalizedPosixPathRelativeToTheWorkspaceRoot: "",
              severity: "ERROR",
              message: result.issue,
              type: "PROBLEM",
              position: {
                startLineNumber: location.rowInFile,
                startColumn: 0,
                endLineNumber: 0,
                endColumn: 0,
              },
            });
          }
        }
      } catch (e) {
        // We add nothing since json is malformed.
      }
      resolve(notifications);
      worker.terminate();
    };
  });
}
