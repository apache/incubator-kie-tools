/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import * as fs from "fs";
import * as path from "path";
import { SwfYamlOffsets } from "@kie-tools/serverless-workflow-language-service/dist/editor";
import { getLineFromOffset } from "./testUtils";

describe("SwfYamlOffsets tests", () => {
  const swfYamlOffsets = new SwfYamlOffsets();
  const inputFilesBasePath = path.join("tests", "inputFiles", "yaml") + "/";
  const allInputFiles = [["helloState.sw.yaml"], ["greeting.sw.yaml"], ["greeting-long.sw.yaml"]];
  const allInputFilesFullText = new Map(
    allInputFiles.map((item) => [item[0], fs.readFileSync(inputFilesBasePath + item[0], "utf8").toString()])
  );

  describe("parseContent", () => {
    let getAllOffsetsSpy: any;

    beforeEach(() => {
      getAllOffsetsSpy = jest.spyOn(swfYamlOffsets, "getAllOffsets");
    });

    it("should not call getAllOffsets() with wrong inputs", () => {
      // @ts-ignore
      swfYamlOffsets.parseContent(null);
      expect(getAllOffsetsSpy).not.toHaveBeenCalled();

      // @ts-ignore
      swfYamlOffsets.parseContent();
      expect(getAllOffsetsSpy).not.toHaveBeenCalled();

      swfYamlOffsets.parseContent("");
      expect(getAllOffsetsSpy).not.toHaveBeenCalled();
    });

    it("should call getAllOffsets() with good content", () => {
      swfYamlOffsets.parseContent(allInputFilesFullText.get("helloState.sw.yaml")!);
      expect(getAllOffsetsSpy).toHaveBeenCalled();
    });

    it("should not call getAllOffsets() with unchanged content", () => {
      swfYamlOffsets.parseContent(allInputFilesFullText.get("helloState.sw.yaml")!);
      expect(getAllOffsetsSpy).not.toHaveBeenCalled();
    });

    afterEach(() => {
      getAllOffsetsSpy.mockRestore();
    });
  });

  describe("getFullAST", () => {
    it("Should return null with wrong inputs", () => {
      expect(swfYamlOffsets.parseContent("").getFullAST()).toStrictEqual(null);
      // @ts-ignore
      expect(swfYamlOffsets.parseContent(null).getFullAST()).toStrictEqual(null);
      // @ts-ignore
      expect(swfYamlOffsets.parseContent().getFullAST()).toStrictEqual(null);
      expect(swfYamlOffsets.parseContent('{"notValid":').getFullAST()).toStrictEqual(null);
    });

    it.each(allInputFiles)("Should return a valid object parsing the input file %s", (fileName) => {
      swfYamlOffsets.parseContent(allInputFilesFullText.get(fileName)!);

      expect(swfYamlOffsets.getFullAST()).toHaveProperty("mappings");
    });
  });

  describe("getAllOffsets", () => {
    it("Should return an empty offsets object with wrong inputs", () => {
      const emptyOffsets = { states: {} };
      // @ts-ignore
      expect(swfYamlOffsets.parseContent(null).getAllOffsets()).toStrictEqual(emptyOffsets);
      // @ts-ignore
      expect(swfYamlOffsets.parseContent().getAllOffsets()).toStrictEqual(emptyOffsets);
      expect(swfYamlOffsets.parseContent("").getAllOffsets()).toStrictEqual(emptyOffsets);
      expect(swfYamlOffsets.parseContent('{"notValid":').getAllOffsets()).toStrictEqual(emptyOffsets);
    });

    it("Should return a valid object parsing the input file 'helloState.sw.yaml'", () => {
      const fileName = "helloState.sw.yaml";
      const fullText = allInputFilesFullText.get(fileName)!;
      const offsets = swfYamlOffsets.parseContent(fullText).getAllOffsets();

      expect(getLineFromOffset(fullText, offsets.states["Hello State"].stateNameOffset)).toContain("name: Hello State");
      expect(getLineFromOffset(fullText, offsets.states["Hello State Two"].offset.start)).toContain(
        "name: Hello State Two"
      );
      expect(offsets.states["Hello State Two"].offset.end).toBe(290);
    });

    it("Should return a valid object parsing the input file 'greeting.sw.yaml'", () => {
      const fileName = "greeting.sw.yaml";
      const fullText = allInputFilesFullText.get(fileName)!;
      const offsets = swfYamlOffsets.parseContent(fullText).getAllOffsets();

      expect(getLineFromOffset(fullText, offsets.states["GreetInEnglish"].stateNameOffset)).toContain(
        "name: GreetInEnglish"
      );
      expect(getLineFromOffset(fullText, offsets.states["GetGreeting"].offset.start)).toContain("name: GetGreeting");
      expect(offsets.states["GetGreeting"].offset.end).toBe(1228);
    });

    it("Should return a valid object parsing the input file 'greeting-long.sw.yaml'", () => {
      const fileName = "greeting-long.sw.yaml";
      const fullText = allInputFilesFullText.get(fileName)!;
      const offsets = swfYamlOffsets.parseContent(fullText).getAllOffsets();

      expect(getLineFromOffset(fullText, offsets.states["GreetInEnglish"].stateNameOffset)).toContain(
        "name: GreetInEnglish"
      );
      expect(getLineFromOffset(fullText, offsets.states["GreetInPortuguese 2"].offset.start)).toContain(
        "name: GreetInPortuguese 2"
      );
      expect(offsets.states["GreetInEnglish 4"].offset.end).toBe(1672);
    });
  });

  describe("getStateNameOffset", () => {
    const fullText = allInputFilesFullText.get("helloState.sw.yaml")!;
    const helloStateYamlOffsets = swfYamlOffsets.parseContent(fullText);

    it("Should return undefined with wrong inputs", () => {
      expect(swfYamlOffsets.parseContent("").getStateNameOffset("Hello State")).toBe(undefined);
      // @ts-ignore
      expect(helloStateYamlOffsets.getStateNameOffset()).toBe(undefined);
      // @ts-ignore
      expect(helloStateYamlOffsets.getStateNameOffset(null)).toBe(undefined);
      expect(helloStateYamlOffsets.getStateNameOffset("")).toBe(undefined);
    });

    it("Should return undefined if the state is not found", () => {
      expect(() => {
        helloStateYamlOffsets.getStateNameOffset("Not a state");
      }).not.toThrowError();
      expect(helloStateYamlOffsets.getStateNameOffset("Not a state")).toBe(undefined);
    });

    it.each([
      ["helloState.sw.yaml", "Hello State"],
      ["helloState.sw.yaml", "Hello State Two"],
      ["greeting.sw.yaml", "GreetInEnglish"],
      ["greeting.sw.yaml", "GetGreeting"],
    ])(
      'On file %s, getStateNameOffset() with state name "%s" should return a correct offset',
      (fileName, stateName) => {
        const fullText = allInputFilesFullText.get(fileName)!;
        swfYamlOffsets.parseContent(fullText);
        expect(getLineFromOffset(fullText, swfYamlOffsets.getStateNameOffset(stateName))).toContain(stateName);
      }
    );
  });

  describe("getStateNameFromOffset", () => {
    const fullText = allInputFilesFullText.get("helloState.sw.yaml")!;
    const helloStateYamlOffsets = swfYamlOffsets.parseContent(fullText);

    it("Should return null with wrong inputs", () => {
      expect(swfYamlOffsets.parseContent("").getStateNameFromOffset(-1)).toBeNull();
      // @ts-ignore
      expect(helloStateYamlOffsets.getStateNameFromOffset()).toBeNull();
      // @ts-ignore
      expect(helloStateYamlOffsets.getStateNameFromOffset(0)).toBeNull();
    });

    it("Should return null if the state is not found", () => {
      expect(helloStateYamlOffsets.getStateNameFromOffset(999999)).toBeNull();
      expect(() => {
        helloStateYamlOffsets.getStateNameFromOffset(100);
      }).not.toThrowError();
      expect(helloStateYamlOffsets.getStateNameFromOffset(100)).toBeNull();
    });

    it.each([
      ["helloState.sw.yaml", "Hello State"],
      ["helloState.sw.yaml", "Hello State Two"],
      ["greeting.sw.yaml", "GreetInEnglish"],
      ["greeting.sw.yaml", "GetGreeting"],
    ])('On file %s, with the offset of "%s" should return the correct state name', (fileName, stateName) => {
      const fullText = allInputFilesFullText.get(fileName)!;
      swfYamlOffsets.parseContent(fullText);
      const offset = swfYamlOffsets.getStateNameOffset(stateName);

      expect(swfYamlOffsets.getStateNameFromOffset(offset! + 50)).toBe(stateName);
    });
  });
});
