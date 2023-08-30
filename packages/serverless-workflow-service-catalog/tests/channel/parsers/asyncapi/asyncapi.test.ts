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

import * as fs from "fs";
import * as path from "path";
import * as yaml from "js-yaml";
import { parseAsyncApi } from "@kie-tools/serverless-workflow-service-catalog/dist/channel";
import {
  SwfServiceCatalogFunctionArgumentType,
  SwfServiceCatalogFunctionType,
  SwfServiceCatalogService,
  SwfCatalogSourceType,
  SwfServiceCatalogServiceType,
} from "@kie-tools/serverless-workflow-service-catalog/dist/api";

function doParse(fileName: string): SwfServiceCatalogService {
  const filePath = path.resolve(__dirname, `../examples/${fileName}`);
  const content = fs.readFileSync(filePath).toString("utf-8");
  const serviceAsyncApiDocument = yaml.load(content) as any;

  return parseAsyncApi(
    {
      serviceFileName: fileName,
      serviceFileContent: content,
      source: {
        type: SwfCatalogSourceType?.LOCAL_FS,
        absoluteFilePath: `/async-api-tests/specs/${fileName}`,
      },
    },
    serviceAsyncApiDocument
  );
}

describe("asyncapi parser", () => {
  it("parse message asyncapi", async () => {
    const result = doParse("message.yaml");

    expect(result).not.toBeNull();
    expect(result.type).toBe(SwfServiceCatalogServiceType.asyncapi);
    expect(result.source.type).toBe(SwfCatalogSourceType.LOCAL_FS);
    expect(result.source).toHaveProperty("absoluteFilePath", "/async-api-tests/specs/message.yaml");
    expect(result.name).toBe("Kafka Application");

    expect(result.functions).toHaveLength(1);

    const subscribeOperation = result.functions[0];
    expect(subscribeOperation.type).toBe(SwfServiceCatalogFunctionType.asyncapi);
    expect(subscribeOperation.name).toBe("wait");
    expect(subscribeOperation.arguments).not.toBeNull();
  });

  it("parse http asyncapi", async () => {
    const result = doParse("http.yaml");

    expect(result).not.toBeNull();
    expect(result.type).toBe(SwfServiceCatalogServiceType.asyncapi);
    expect(result.source.type).toBe(SwfCatalogSourceType.LOCAL_FS);
    expect(result.source).toHaveProperty("absoluteFilePath", "/async-api-tests/specs/http.yaml");
    expect(result.name).toBe("Http Application");
    expect(result.functions).toHaveLength(2);

    const functionDef = result.functions[0];
    expect(functionDef.type).toBe(SwfServiceCatalogFunctionType.asyncapi);
    expect(functionDef.name).toBe("getMessages");
    expect(functionDef.arguments).not.toBeNull();

    expect(functionDef.arguments).toHaveProperty("userId", SwfServiceCatalogFunctionArgumentType.string);
    expect(functionDef.arguments).toHaveProperty("age", SwfServiceCatalogFunctionArgumentType.integer);
    expect(functionDef.arguments).toHaveProperty("name", SwfServiceCatalogFunctionArgumentType.string);
  });
});
