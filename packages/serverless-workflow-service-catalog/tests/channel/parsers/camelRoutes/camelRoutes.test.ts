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
import { parseCamelRoutes } from "@kie-tools/serverless-workflow-service-catalog/dist/channel";
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
  const serviceCamelRoutesDocument = yaml.load(content) as any;

  return parseCamelRoutes(
    {
      serviceFileName: fileName,
      serviceFileContent: content,
      source: {
        type: SwfCatalogSourceType?.LOCAL_FS,
        absoluteFilePath: `/camel-routes-tests/specs/${fileName}`,
      },
    },
    serviceCamelRoutesDocument
  );
}

describe("Camel routes parser", () => {
  it("parse numberToWords camelRoutes", async () => {
    const result = doParse("numberToWords.yaml");
    expect(result).not.toBeNull();
    expect(result.type).toBe(SwfServiceCatalogServiceType.camelroute);
    expect(result.source.type).toBe(SwfCatalogSourceType.LOCAL_FS);
    expect(result.source).toHaveProperty("absoluteFilePath", "/camel-routes-tests/specs/numberToWords.yaml");
    expect(result.name).toBe("numberToWords");

    expect(result.functions).toHaveLength(2);

    const subscribeOperation = result.functions[0];
    expect(subscribeOperation.type).toBe(SwfServiceCatalogFunctionType.custom);
    expect(subscribeOperation.name).toBe("camel:direct:numberToWords");
    expect(subscribeOperation.arguments).not.toBeNull();
  });

  it("parse camelMessage routes", async () => {
    const result = doParse("sendMessage.yaml");
    expect(result).not.toBeNull();
    expect(result.type).toBe(SwfServiceCatalogServiceType.camelroute);
    expect(result.source.type).toBe(SwfCatalogSourceType.LOCAL_FS);
    expect(result.source).toHaveProperty("absoluteFilePath", "/camel-routes-tests/specs/sendMessage.yaml");
    expect(result.name).toBe("sendMessage");
    expect(result.functions).toHaveLength(1);
    const functionDef = result.functions[0];
    expect(functionDef.type).toBe(SwfServiceCatalogFunctionType.custom);
    expect(functionDef.name).toBe("camel:direct:sendMessage");
    expect(functionDef.arguments).not.toBeNull();
    expect(functionDef.arguments).toHaveProperty("body", SwfServiceCatalogFunctionArgumentType.string);
    expect(functionDef.arguments).toHaveProperty("header", SwfServiceCatalogFunctionArgumentType.object);
  });
});
