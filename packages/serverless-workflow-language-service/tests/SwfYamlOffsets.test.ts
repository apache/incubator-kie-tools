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

import { parseYamlContent } from "@kie-tools/json-yaml-language-service/dist/channel";
import {
  getAllStateOffsets,
  getYamlStateNameOffset,
  getYamlStateNameFromOffset,
} from "@kie-tools/serverless-workflow-language-service/dist/editor";
import * as fs from "fs";
import * as path from "path";
import { getLineFromOffset } from "./testUtils";

describe("SwfYamlOffsets tests", () => {
  const inputFilesBasePath = path.join("tests", "inputFiles", "yaml") + "/";
  const allInputFiles = [["helloState.sw.yaml"], ["greeting.sw.yaml"], ["greeting-long.sw.yaml"]];
  const allInputFilesFullText = new Map(
    allInputFiles.map((item) => [item[0], fs.readFileSync(inputFilesBasePath + item[0], "utf8").toString()])
  );

  describe("getAllStateOffsets", () => {
    it("Should return a valid object parsing the input file 'helloState.sw.yaml'", () => {
      const fileName = "helloState.sw.yaml";
      const content = allInputFilesFullText.get(fileName)!;
      const rootNode = parseYamlContent(content);
      const offsets = getAllStateOffsets({ rootNode });

      expect(getLineFromOffset(content, offsets.states["Hello State"].stateNameOffset)).toContain("name: Hello State");
      expect(getLineFromOffset(content, offsets.states["Hello State Two"].offset.start)).toContain(
        "name: Hello State Two"
      );
      expect(offsets.states["Hello State Two"].offset.end).toBe(290);
    });

    it("Should return a valid object parsing the input file 'greeting.sw.yaml'", () => {
      const fileName = "greeting.sw.yaml";
      const content = allInputFilesFullText.get(fileName)!;
      const rootNode = parseYamlContent(content);
      const offsets = getAllStateOffsets({ rootNode });

      expect(getLineFromOffset(content, offsets.states["GreetInEnglish"].stateNameOffset)).toContain(
        "name: GreetInEnglish"
      );
      expect(getLineFromOffset(content, offsets.states["GetGreeting"].offset.start)).toContain("name: GetGreeting");
      expect(offsets.states["GetGreeting"].offset.end).toBe(1228);
    });

    it("Should return a valid object parsing the input file 'greeting-long.sw.yaml'", () => {
      const fileName = "greeting-long.sw.yaml";
      const content = allInputFilesFullText.get(fileName)!;
      const rootNode = parseYamlContent(content);
      const offsets = getAllStateOffsets({ rootNode });

      expect(getLineFromOffset(content, offsets.states["GreetInEnglish"].stateNameOffset)).toContain(
        "name: GreetInEnglish"
      );
      expect(getLineFromOffset(content, offsets.states["GreetInPortuguese 2"].offset.start)).toContain(
        "name: GreetInPortuguese 2"
      );
      expect(offsets.states["GreetInEnglish 4"].offset.end).toBe(1672);
    });
  });

  describe("getYamlStateNameOffset", () => {
    it("Should return undefined if the state is not found", () => {
      const content = allInputFilesFullText.get("helloState.sw.yaml")!;
      expect(getYamlStateNameOffset({ content: content, stateName: "Not a state" })).toBe(undefined);
    });

    it.each([
      ["helloState.sw.yaml", "Hello State"],
      ["helloState.sw.yaml", "Hello State Two"],
      ["greeting.sw.yaml", "GreetInEnglish"],
      ["greeting.sw.yaml", "GetGreeting"],
    ])(
      'On file %s, getYamlStateNameOffset() with state name "%s" should return a correct offset',
      (fileName, stateName) => {
        const content = allInputFilesFullText.get(fileName)!;

        expect(getLineFromOffset(content, getYamlStateNameOffset({ content, stateName }))).toContain(stateName);
      }
    );
  });

  describe("getYamlStateNameFromOffset", () => {
    it("Should return null if the state is not found", () => {
      const content = allInputFilesFullText.get("helloState.sw.yaml")!;
      expect(getYamlStateNameFromOffset({ content, offset: 999999 })).toBeUndefined();
      expect(() => {
        getYamlStateNameFromOffset({ content, offset: 100 });
      }).not.toThrowError();
      expect(getYamlStateNameFromOffset({ content, offset: 100 })).toBeUndefined();
    });

    it.each([
      ["helloState.sw.yaml", "Hello State"],
      ["helloState.sw.yaml", "Hello State Two"],
      ["greeting.sw.yaml", "GreetInEnglish"],
      ["greeting.sw.yaml", "GetGreeting"],
    ])('On file %s, with the offset of "%s" should return the correct state name', async (fileName, stateName) => {
      const content = allInputFilesFullText.get(fileName)!;
      const offset = getYamlStateNameOffset({ content, stateName });

      expect(getYamlStateNameFromOffset({ content, offset: offset! + 50 })).toBe(stateName);
    });
  });
});
