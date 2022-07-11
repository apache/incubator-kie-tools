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
import { SwfJsonOffsets } from "@kie-tools/serverless-workflow-language-service/dist/editor";
import { getLineFromOffset } from "./testUtils";

describe("SwfJsonOffsets tests", () => {
  const swfJsonOffsets = new SwfJsonOffsets();
  const inputFilesBasePath = path.join("tests", "inputFiles", "json") + "/";
  const allInputFiles = [["helloState.sw.json"], ["greeting.sw.json"], ["greeting-long.sw.json"]];
  const allInputFilesFullText = new Map(
    allInputFiles.map((item) => [item[0], fs.readFileSync(inputFilesBasePath + item[0], "utf8").toString()])
  );

  describe("parseContent", () => {
    let getAllOffsetsSpy: any;

    beforeEach(() => {
      getAllOffsetsSpy = jest.spyOn(swfJsonOffsets, "getAllOffsets");
    });

    it("should not call getAllOffsets() with wrong inputs", () => {
      // @ts-ignore
      swfJsonOffsets.parseContent(null);
      expect(getAllOffsetsSpy).not.toHaveBeenCalled();

      // @ts-ignore
      swfJsonOffsets.parseContent();
      expect(getAllOffsetsSpy).not.toHaveBeenCalled();

      swfJsonOffsets.parseContent("");
      expect(getAllOffsetsSpy).not.toHaveBeenCalled();
    });

    it("should call getAllOffsets() with good content", () => {
      swfJsonOffsets.parseContent(allInputFilesFullText.get("helloState.sw.json")!);
      expect(getAllOffsetsSpy).toHaveBeenCalled();
    });

    it("should not call getAllOffsets() with unchanged content", () => {
      swfJsonOffsets.parseContent(allInputFilesFullText.get("helloState.sw.json")!);
      expect(getAllOffsetsSpy).not.toHaveBeenCalled();
    });

    afterEach(() => {
      getAllOffsetsSpy.mockRestore();
    });
  });

  describe("getFullAST", () => {
    it("Should return null with wrong inputs", () => {
      expect(swfJsonOffsets.parseContent("").getFullAST()).toStrictEqual(null);
      // @ts-ignore
      expect(swfJsonOffsets.parseContent(null).getFullAST()).toStrictEqual(null);
      // @ts-ignore
      expect(swfJsonOffsets.parseContent().getFullAST()).toStrictEqual(null);
      expect(swfJsonOffsets.parseContent('{"notValid":').getFullAST()).toStrictEqual(null);
    });

    it.each(allInputFiles)("Should return a valid object parsing the input file %s", (fileName) => {
      swfJsonOffsets.parseContent(allInputFilesFullText.get(fileName)!);

      expect(swfJsonOffsets.getFullAST()).toHaveProperty("children");
    });
  });

  describe("getAllOffsets", () => {
    it("Should return an empty offsets object with wrong inputs", () => {
      const emptyOffsets = { states: {} };
      // @ts-ignore
      expect(swfJsonOffsets.parseContent(null).getAllOffsets()).toStrictEqual(emptyOffsets);
      // @ts-ignore
      expect(swfJsonOffsets.parseContent().getAllOffsets()).toStrictEqual(emptyOffsets);
      expect(swfJsonOffsets.parseContent("").getAllOffsets()).toStrictEqual(emptyOffsets);
      expect(swfJsonOffsets.parseContent('{"notValid":').getAllOffsets()).toStrictEqual(emptyOffsets);
    });

    it("Should return a valid object parsing the input file 'helloState.sw.json'", () => {
      const fileName = "helloState.sw.json";
      const fullText = allInputFilesFullText.get(fileName)!;
      const offsets = swfJsonOffsets.parseContent(fullText).getAllOffsets();

      expect(getLineFromOffset(fullText, offsets.states["Hello State"].stateNameOffset)).toContain("Hello State");
      expect(getLineFromOffset(fullText, offsets.states["Hello State Two"].offset.start, 1)).toContain(
        "Hello State Two"
      );
      expect(offsets.states["Hello State Two"].offset.end).toBe(428);
    });

    it("Should return a valid object parsing the input file 'greeting.sw.json'", () => {
      const fileName = "greeting.sw.json";
      const fullText = allInputFilesFullText.get(fileName)!;
      const offsets = swfJsonOffsets.parseContent(fullText).getAllOffsets();

      expect(getLineFromOffset(fullText, offsets.states["GreetInEnglish"].stateNameOffset)).toContain("GreetInEnglish");
      expect(getLineFromOffset(fullText, offsets.states["GetGreeting"].offset.start, 1)).toContain("GetGreeting");
      expect(offsets.states["GetGreeting"].offset.end).toBe(1770);
    });

    it("Should return a valid object parsing the input file 'greeting-long.sw.json'", () => {
      const fileName = "greeting-long.sw.json";
      const fullText = allInputFilesFullText.get(fileName)!;
      const offsets = swfJsonOffsets.parseContent(fullText).getAllOffsets();

      expect(getLineFromOffset(fullText, offsets.states["GreetInEnglish"].stateNameOffset)).toContain("GreetInEnglish");
      expect(getLineFromOffset(fullText, offsets.states["GreetInPortuguese 2"].offset.start, 1)).toContain(
        "GreetInPortuguese 2"
      );
      expect(offsets.states["GreetInEnglish 4"].offset.end).toBe(2446);
    });
  });

  describe("getStateNameOffset", () => {
    const fullText = allInputFilesFullText.get("helloState.sw.json")!;
    const helloStateJsonOffsets = swfJsonOffsets.parseContent(fullText);

    it("Should return undefined with wrong inputs", () => {
      expect(swfJsonOffsets.parseContent("").getStateNameOffset("Hello State")).toBe(undefined);
      // @ts-ignore
      expect(helloStateJsonOffsets.getStateNameOffset()).toBe(undefined);
      // @ts-ignore
      expect(helloStateJsonOffsets.getStateNameOffset(null)).toBe(undefined);
      expect(helloStateJsonOffsets.getStateNameOffset("")).toBe(undefined);
    });

    it("Should return undefined if the state is not found", () => {
      expect(() => {
        helloStateJsonOffsets.getStateNameOffset("Not a state");
      }).not.toThrowError();
      expect(helloStateJsonOffsets.getStateNameOffset("Not a state")).toBe(undefined);
    });

    it.each([
      ["helloState.sw.json", "Hello State"],
      ["helloState.sw.json", "Hello State Two"],
      ["greeting.sw.json", "GreetInEnglish"],
      ["greeting.sw.json", "GetGreeting"],
    ])(
      'On file %s, getStateNameOffset() with state name "%s" should return a correct offset',
      (fileName, stateName) => {
        const fullText = allInputFilesFullText.get(fileName)!;
        swfJsonOffsets.parseContent(fullText);
        expect(getLineFromOffset(fullText, swfJsonOffsets.getStateNameOffset(stateName))).toContain(stateName);
      }
    );
  });

  describe("getStateNameFromOffset", () => {
    const fullText = allInputFilesFullText.get("helloState.sw.json")!;
    const helloStateJsonOffsets = swfJsonOffsets.parseContent(fullText);

    it("Should return null with wrong inputs", () => {
      expect(swfJsonOffsets.parseContent("").getStateNameFromOffset(-1)).toBeNull();
      // @ts-ignore
      expect(helloStateJsonOffsets.getStateNameFromOffset()).toBeNull();
      // @ts-ignore
      expect(helloStateJsonOffsets.getStateNameFromOffset(0)).toBeNull();
    });

    it("Should return null if the state is not found", () => {
      expect(helloStateJsonOffsets.getStateNameFromOffset(999999)).toBeNull();
      expect(() => {
        helloStateJsonOffsets.getStateNameFromOffset(100);
      }).not.toThrowError();
      expect(helloStateJsonOffsets.getStateNameFromOffset(100)).toBeNull();
    });

    it.each([
      ["helloState.sw.json", "Hello State"],
      ["helloState.sw.json", "Hello State Two"],
      ["greeting.sw.json", "GreetInEnglish"],
      ["greeting.sw.json", "GetGreeting"],
    ])('On file %s, with the offset of "%s" should return the correct state name', (fileName, stateName) => {
      const fullText = allInputFilesFullText.get(fileName)!;
      swfJsonOffsets.parseContent(fullText);
      const offset = swfJsonOffsets.getStateNameOffset(stateName);

      expect(swfJsonOffsets.getStateNameFromOffset(offset! + 50)).toBe(stateName);
    });
  });
});
