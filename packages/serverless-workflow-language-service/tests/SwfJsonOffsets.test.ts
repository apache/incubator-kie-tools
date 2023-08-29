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

import { parseJsonContent } from "@kie-tools/json-yaml-language-service/dist/channel";
import {
  getAllStateOffsets,
  getJsonStateNameOffset,
  getJsonStateNameFromOffset,
} from "@kie-tools/serverless-workflow-language-service/dist/editor";
import * as fs from "fs";
import * as path from "path";
import { getLineFromOffset } from "./testUtils";

describe("SwfJsonOffsets tests", () => {
  const inputFilesBasePath = path.join("tests", "inputFiles", "json") + "/";
  const allInputFiles = [["helloState.sw.json"], ["greeting.sw.json"], ["greeting-long.sw.json"]];
  const allInputFilesFullText = new Map(
    allInputFiles.map((item) => [item[0], fs.readFileSync(inputFilesBasePath + item[0], "utf8").toString()])
  );

  describe("getAllStateOffsets", () => {
    it("Should return a valid object parsing the input file 'helloState.sw.json'", () => {
      const fileName = "helloState.sw.json";
      const content = allInputFilesFullText.get(fileName)!;
      const rootNode = parseJsonContent(content);
      const offsets = getAllStateOffsets({ rootNode });

      expect(getLineFromOffset(content, offsets.states["Hello State"].stateNameOffset)).toContain("Hello State");
      expect(getLineFromOffset(content, offsets.states["Hello State Two"].offset.start, 1)).toContain(
        "Hello State Two"
      );
      expect(offsets.states["Hello State Two"].offset.end).toBe(428);
    });

    it("Should return a valid object parsing the input file 'greeting.sw.json'", () => {
      const fileName = "greeting.sw.json";
      const content = allInputFilesFullText.get(fileName)!;
      const rootNode = parseJsonContent(content);
      const offsets = getAllStateOffsets({ rootNode });

      expect(getLineFromOffset(content, offsets.states["GreetInEnglish"].stateNameOffset)).toContain("GreetInEnglish");
      expect(getLineFromOffset(content, offsets.states["GetGreeting"].offset.start, 1)).toContain("GetGreeting");
      expect(offsets.states["GetGreeting"].offset.end).toBe(1770);
    });

    it("Should return a valid object parsing the input file 'greeting-long.sw.json'", () => {
      const fileName = "greeting-long.sw.json";
      const content = allInputFilesFullText.get(fileName)!;
      const rootNode = parseJsonContent(content);
      const offsets = getAllStateOffsets({ rootNode });

      expect(getLineFromOffset(content, offsets.states["GreetInEnglish"].stateNameOffset)).toContain("GreetInEnglish");
      expect(getLineFromOffset(content, offsets.states["GreetInPortuguese 2"].offset.start, 1)).toContain(
        "GreetInPortuguese 2"
      );
      expect(offsets.states["GreetInEnglish 4"].offset.end).toBe(2446);
    });
  });

  describe("getJsonStateNameOffset", () => {
    it("Should return undefined if the state is not found", async () => {
      const content = allInputFilesFullText.get("helloState.sw.json")!;
      expect(getJsonStateNameOffset({ content, stateName: "Not a state" })).toBe(undefined);
    });

    it.each([
      ["helloState.sw.json", "Hello State"],
      ["helloState.sw.json", "Hello State Two"],
      ["greeting.sw.json", "GreetInEnglish"],
      ["greeting.sw.json", "GetGreeting"],
    ])(
      'On file %s, getJsonStateNameOffset() with state name "%s" should return a correct offset',
      async (fileName, stateName) => {
        const content = allInputFilesFullText.get(fileName)!;

        expect(getLineFromOffset(content, getJsonStateNameOffset({ content, stateName }))).toContain(stateName);
      }
    );
  });

  describe("getJsonStateNameFromOffset", () => {
    it("Should return null if the state is not found", () => {
      const content = allInputFilesFullText.get("helloState.sw.json")!;
      expect(getJsonStateNameFromOffset({ content, offset: 999999 })).toBeUndefined();
      expect(() => {
        getJsonStateNameFromOffset({ content, offset: 100 });
      }).not.toThrowError();
      expect(getJsonStateNameFromOffset({ content, offset: 100 })).toBeUndefined();
    });

    it.each([
      ["helloState.sw.json", "Hello State"],
      ["helloState.sw.json", "Hello State Two"],
      ["greeting.sw.json", "GreetInEnglish"],
      ["greeting.sw.json", "GetGreeting"],
    ])('On file %s, with the offset of "%s" should return the correct state name', async (fileName, stateName) => {
      const content = allInputFilesFullText.get(fileName)!;
      const offset = getJsonStateNameOffset({ content, stateName });

      expect(getJsonStateNameFromOffset({ content, offset: offset! + 50 })).toBe(stateName);
    });
  });
});
