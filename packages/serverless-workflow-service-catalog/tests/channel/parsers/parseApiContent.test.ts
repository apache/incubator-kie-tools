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
import { SwfCatalogSourceType, SwfServiceCatalogService, SwfServiceCatalogServiceType } from "../../../dist/api";
import { parseApiContent } from "../../../dist/channel";

function parseContent(fileName: string): SwfServiceCatalogService {
  const filePath = path.resolve(__dirname, `examples/${fileName}`);
  const content = fs.readFileSync(filePath).toString("utf-8");

  return parseApiContent({
    serviceFileName: fileName,
    serviceFileContent: content,
    source: {
      type: SwfCatalogSourceType?.LOCAL_FS,
      absoluteFilePath: `/Users/tiago/open-api-tests/specs/${fileName}`,
    },
  });
}

describe("parserApiContent tests", () => {
  it("parse asyncapi file", async () => {
    const parserResults = parseContent("message.yaml");
    expect(parserResults).not.toBeNull();
    expect(parserResults.type).toBe(SwfServiceCatalogServiceType.asyncapi);
    expect(parserResults.name).toBe("Kafka Application");
    expect(parserResults.source.type).toBe(SwfCatalogSourceType.LOCAL_FS);
    expect(parserResults.functions).toHaveLength(1);
  });
  it("parse openapi file", async () => {
    const parserResults = parseContent("greeting.yaml");
    expect(parserResults).not.toBeNull();
    expect(parserResults.type).toBe(SwfServiceCatalogServiceType.rest);
    expect(parserResults.name).toBe("quarkus-example API");
    expect(parserResults.source.type).toBe(SwfCatalogSourceType.LOCAL_FS);
    expect(parserResults.functions).toHaveLength(1);
  });
  it("parse jsonschema file", async () => {
    const parserResults = parseContent("expression.json");
    expect(parserResults).not.toBeNull();
    expect(parserResults.type).toBe("jsonschema" as SwfCatalogSourceType);
    expect(parserResults.name).toBe("Expression");
    expect(parserResults.source.type).toBe(SwfCatalogSourceType.LOCAL_FS);
    expect(parserResults.functions).toHaveLength(1);
  });
  it("parse wrong spec file", async () => {
    expect(() => {
      parseContent("wrong.txt");
    }).toThrow("'wrong.txt' is not a supported spec file");

    expect(() => {
      parseContent("wrong.json");
    }).toThrowError("'wrong.json' is not a supported spec file");
  });
});
