/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as fs from "fs";
import * as path from "path";
import { parseOpenAPI } from "@kie-tools/service-catalog/dist/channel";
import { FunctionArgumentType, FunctionType, Service, ServiceType } from "@kie-tools/service-catalog/dist/api";

function doParse(fileName: string): Service {
  const filePath = path.resolve(__dirname, `examples/${fileName}`);
  const content = fs.readFileSync(filePath).toString("utf-8");

  return parseOpenAPI({
    fileName,
    content,
    storagePath: "specs",
  });
}

describe("openapi parser", () => {
  it("parse multiplication openapi", async () => {
    const result = doParse("multiplication.yaml");

    expect(result).not.toBeNull();
    expect(result.type).toBe(ServiceType.rest);
    expect(result.id).toBe("specs/multiplication.yaml");
    expect(result.name).toBe("Generated API");

    expect(result.functions).toHaveLength(1);

    const functionDef = result.functions[0];
    expect(functionDef.type).toBe(FunctionType.rest);
    expect(functionDef.name).toBe("doOperation");
    expect(functionDef.operation).toBe("specs/multiplication.yaml#doOperation");
    expect(functionDef.arguments).not.toBeNull();
    expect(functionDef.arguments).toHaveProperty("leftElement", FunctionArgumentType.number);
    expect(functionDef.arguments).toHaveProperty("product", FunctionArgumentType.number);
    expect(functionDef.arguments).toHaveProperty("rightElement", FunctionArgumentType.number);
  });

  it("parse hiring openapi", async () => {
    const result = doParse("hiring.yaml");

    expect(result).not.toBeNull();
    expect(result.type).toBe(ServiceType.rest);
    expect(result.id).toBe("specs/hiring.yaml");
    expect(result.name).toBe("process-usertasks-timer-quarkus-with-console API");

    expect(result.functions).toHaveLength(1);

    const functionDef = result.functions[0];
    expect(functionDef.type).toBe(FunctionType.rest);
    expect(functionDef.name).toBe("hiring");
    expect(functionDef.operation).toBe("specs/hiring.yaml#hiring");
    expect(functionDef.arguments).not.toBeNull();
    expect(functionDef.arguments).toHaveProperty("candidate", FunctionArgumentType.object);
    expect(functionDef.arguments).toHaveProperty("hr_approval", FunctionArgumentType.boolean);
    expect(functionDef.arguments).toHaveProperty("it_approval", FunctionArgumentType.boolean);
  });

  it("parse wrong format test", async () => {
    expect(() => {
      doParse("wrong.txt");
    }).toThrowError("Invalid format");

    expect(() => {
      doParse("wrong.json");
    }).toThrowError("Invalid format");
  });
});
