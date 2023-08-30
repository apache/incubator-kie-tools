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

import crossFetch from "cross-fetch";
import mockSchema from "./mockSchema";
import { JqExpressionReadSchemasImpl } from "../src/impl";
import { JqExpressionContentType } from "../src/api/types";
import { TextEncoder, TextDecoder } from "util";
global.TextEncoder = TextEncoder as any;
global.TextDecoder = TextDecoder as any;
jest.mock("cross-fetch", () => {
  return {
    __esModule: true,
    default: jest.fn(),
  };
});
const mockedCrossFetch = crossFetch as jest.Mock;

describe("read schema tests", () => {
  beforeEach(() => {
    mockedCrossFetch.mockClear();
  });
  it("get data from remote url test", async () => {
    mockedCrossFetch.mockResolvedValue({
      status: 200,
      text: () => JSON.stringify(mockSchema),
    });
    const readSchema = new JqExpressionReadSchemasImpl();
    const result: JqExpressionContentType[] = await readSchema.getContentFromRemoteUrl(["https://mocked_url"]);
    expect(result[0].fileName).toStrictEqual("mocked_url");
    expect(result[0].absoluteFilePath).toStrictEqual("https://mocked_url");
    expect(result[0].fileContent.length).toBeGreaterThan(0);
  });
  it("response status greater than 400", async () => {
    mockedCrossFetch.mockResolvedValue({
      status: 400,
      text: () => JSON.stringify(mockSchema),
    });
    const readSchema = new JqExpressionReadSchemasImpl();
    readSchema
      .getContentFromRemoteUrl(["https://mocked_url"])
      .then(() => {})
      .catch((err) => {
        expect(err).toStrictEqual(
          "failed to get content from url, reason:  cannot fetch the data from the server: error code is 400"
        );
      });
  });

  it("parse schema properties", async () => {
    mockedCrossFetch.mockResolvedValue({
      status: 200,
      text: () => JSON.stringify(mockSchema),
    });
    const readSchema = new JqExpressionReadSchemasImpl();
    const schemas: JqExpressionContentType[] = await readSchema.getContentFromRemoteUrl(["https://mocked_url"]);
    const result = readSchema.parseSchemaProperties(schemas);
    expect(result).toStrictEqual([{ numbers: "array" }, { x: "number" }, { y: "number" }]);
  });
});
