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
import { parseJsonSchema } from "@kie-tools/serverless-workflow-service-catalog/dist/channel";
import {
  SwfServiceCatalogService,
  SwfCatalogSourceType,
  SwfServiceCatalogFunctionArgumentType,
} from "@kie-tools/serverless-workflow-service-catalog/dist/api";
function doParse(fileName: string): SwfServiceCatalogService {
  const filePath = path.resolve(__dirname, `../examples/${fileName}`);
  const content = fs.readFileSync(filePath).toString("utf-8");
  const jsonSchemaDocument = JSON.parse(content) as any;

  return parseJsonSchema(
    {
      serviceFileName: fileName,
      serviceFileContent: content,
      source: {
        type: SwfCatalogSourceType?.LOCAL_FS,
        absoluteFilePath: `/open-api-tests/specs/${fileName}`,
      },
    },
    jsonSchemaDocument
  );
}

describe("jsonschema parser", () => {
  it("parse expression json schema", async () => {
    const result = doParse("expression.json");

    expect(result).not.toBeNull();
    expect(result.type).toBe("jsonschema");
    expect(result.source.type).toBe(SwfCatalogSourceType.LOCAL_FS);
    expect(result.source).toHaveProperty("absoluteFilePath", "/open-api-tests/specs/expression.json");
    expect(result.name).toBe("Expression");

    expect(result.functions).toHaveLength(1);
    const schemaContent = JSON.parse(result.rawContent);
    expect(schemaContent.$schema).toBe("http://json-schema.org/draft-04/schema#");
    expect(schemaContent.title).toBe("Expression");
    expect(schemaContent.description).toBe("Schema for expression test");
    expect(schemaContent.properties).not.toBeNull();
    expect(schemaContent.properties.numbers.type).toBe(SwfServiceCatalogFunctionArgumentType.array);
    expect(schemaContent.properties.numbers.items.properties.x.type).toBe(SwfServiceCatalogFunctionArgumentType.number);
    expect(schemaContent.properties.numbers.items.properties.y.type).toBe(SwfServiceCatalogFunctionArgumentType.number);
  });
});
