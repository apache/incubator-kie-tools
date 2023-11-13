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

import { JqExpressionContentType } from "@kie-tools/serverless-workflow-jq-expressions/dist/api/types";
import { JqExpressionReadSchemasImpl } from "@kie-tools/serverless-workflow-jq-expressions/dist/impl";
import * as vscode from "vscode";
const SCHEMA_REGEX = new RegExp("^.*\\.(json|yaml|yml)$");

export class JqExpressionsReadSchemaFromFs extends JqExpressionReadSchemasImpl {
  public async getContentFromFs(relativePaths: string[]): Promise<JqExpressionContentType[]> {
    const promises = relativePaths.map((relativePath: string) => {
      return new Promise<JqExpressionContentType>((resolve, reject) => {
        try {
          const schemaFileAbsolutePosixPathUri = vscode.Uri.parse(relativePath);
          vscode.workspace.fs.stat(schemaFileAbsolutePosixPathUri).then(
            async (stats) => {
              if (!stats || stats.type !== vscode.FileType.File) {
                reject(`Invalid input schema path: ${relativePath}`);
                return;
              }
              const fileName = relativePath.split("/").pop()!;
              if (!SCHEMA_REGEX.test(fileName.toLowerCase())) {
                reject(`Invalid file format, must be a valid json schema: ${fileName}`);
                return;
              }
              const fileContent = await vscode.workspace.fs.readFile(schemaFileAbsolutePosixPathUri);
              return resolve({
                fileName,
                fileContent,
                absoluteFilePath: relativePath,
              });
            },
            (reason) => {
              reject(`could not load specs folder in ${schemaFileAbsolutePosixPathUri}. the reason ${reason}`);
            }
          );
        } catch (e) {
          console.error(e);
          reject(`Could not read data from input schema. ${e}`);
        }
      });
    });
    try {
      return await Promise.all(promises);
    } catch (e) {
      console.error(`failed to get content from the file path, reason: `, e);
      return [];
    }
  }
}
