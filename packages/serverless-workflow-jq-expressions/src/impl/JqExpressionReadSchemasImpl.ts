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

import { JqExpressionReadSchemas } from "../api/JqExpressionReadSchemas";
import { JqExpressionContentType } from "../api/types";
import {
  SwfCatalogSourceType,
  SwfServiceCatalogServiceSource,
} from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import { parseApiContent } from "@kie-tools/serverless-workflow-service-catalog/dist/channel";
import fetch from "cross-fetch";
export class JqExpressionReadSchemasImpl implements JqExpressionReadSchemas {
  public async getContentFromRemoteUrl(remotePaths: string[]): Promise<JqExpressionContentType[]> {
    const promises = remotePaths.map((remotePath: string) => {
      return new Promise<JqExpressionContentType>((resolve, reject) => {
        (async () => {
          try {
            const response = await fetch(remotePath);
            if (response.status >= 400) {
              reject(`cannot fetch the data from the server: error code is ${response.status}`);
              return;
            }
            const resData = await response.text();
            const fileName = remotePath.split("/").pop()!;
            resolve({
              fileName: fileName,
              fileContent: Uint8Array.from(Array.from(resData).map((letter: string) => letter.charCodeAt(0))),
              absoluteFilePath: remotePath,
            });
          } catch (err) {
            console.error(err);
            reject(err);
          }
        })();
      });
    });
    try {
      return await Promise.all(promises);
    } catch (e) {
      console.error(`failed to get content from url, reason: `, e);
      return [];
    }
  }
  public parseSchemaProperties(contentArray: JqExpressionContentType[]): Record<string, string>[] {
    return contentArray.flatMap((content: JqExpressionContentType) =>
      this.getEachProperties({
        fileName: content.fileName,
        fileContent: content.fileContent,
        absoluteFilePath: content.absoluteFilePath,
      })
    );
  }

  private getEachProperties(args: JqExpressionContentType): Record<string, string>[] {
    try {
      const source: SwfServiceCatalogServiceSource = {
        type: SwfCatalogSourceType?.LOCAL_FS,
        absoluteFilePath: args.absoluteFilePath,
      };
      const parsedContent = parseApiContent({
        serviceFileName: args.fileName,
        serviceFileContent: new TextDecoder("utf-8").decode(args.fileContent),
        source,
      });
      return parsedContent.functions.flatMap((func: any) => {
        return Object.entries(func.arguments).map(([argValue, argType]: [string, string]) => {
          return {
            [argValue]: argType,
          };
        });
      });
    } catch (e) {
      console.error(e);
      return [];
    }
  }
}
