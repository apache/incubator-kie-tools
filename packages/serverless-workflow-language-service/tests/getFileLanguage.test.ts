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

import {
  FileLanguage,
  getFileLanguage,
  getFileLanguageOrThrow,
} from "@kie-tools/serverless-workflow-language-service/dist/api";

describe("getFileLanguage", () => {
  it("Checking file language with not valid inputs", () => {
    expect(getFileLanguage("")).toBe(null);
    expect(getFileLanguage(" ")).toBe(null);
  });

  it.each([
    ["not_valid.txt", null],
    ["/home/user/Desktop/not_valid.txt", null],
    ["C:\\Users\\MyName\\Desktop\\not_valid.txt", null],
    ["not_valid.json", null],
    ["not_valid.sw.json.txt", null],
    ["/home/user/Desktop/not_valid.json", null],
    ["C:\\Users\\MyName\\Desktop\\not_valid.json", null],
    ["valid.sw.json", FileLanguage.JSON],
    ["/home/user/Desktop/valid.sw.json", FileLanguage.JSON],
    ["C:\\Users\\MyName\\Desktop\\valid.sw.json", FileLanguage.JSON],
    ["VALID.SW.JSON", FileLanguage.JSON],
    ["/HOME/USER/DESKTOP/VALID.SW.JSON", FileLanguage.JSON],
    ["C:\\USERS\\MYNAME\\DESKTOP\\VALID.SW.JSON", FileLanguage.JSON],
    ["valid.sw.yml", FileLanguage.YAML],
    ["VALID.SW.YML", FileLanguage.YAML],
    ["/home/user/Desktop/valid.sw.yml", FileLanguage.YAML],
    ["C:\\Users\\MyName\\Desktop\\valid.sw.yml", FileLanguage.YAML],
    ["valid.sw.yaml", FileLanguage.YAML],
    ["/home/user/Desktop/valid.sw.yaml", FileLanguage.YAML],
    ["C:\\Users\\MyName\\Desktop\\valid.sw.yaml", FileLanguage.YAML],
    ["VALID.SW.YAML", FileLanguage.YAML],
    ["/HOME/USER/DESKTOP/VALID.SW.YAML", FileLanguage.YAML],
    ["C:\\USERS\\MYNAME\\DESKTOP\\VALID.SW.YAML", FileLanguage.YAML],
  ])("Checking file language of: %s", (fileName, expectFileLanguage) => {
    expect(getFileLanguage(fileName)).toBe(expectFileLanguage);
  });
});

describe("getFileLanguageOrThrow", () => {
  it("Checking file language with not valid inputs", () => {
    expect(() => getFileLanguageOrThrow("")).toThrow(Error);
    expect(() => getFileLanguageOrThrow(" ")).toThrow(Error);
    expect(() => getFileLanguageOrThrow("not_valid.txt")).toThrow(Error);
    expect(() => getFileLanguageOrThrow("/home/user/Desktop/not_valid.txt")).toThrow(Error);
    expect(() => getFileLanguageOrThrow("C:\\Users\\MyName\\Desktop\\not_valid.txt")).toThrow(Error);
    expect(() => getFileLanguageOrThrow("not_valid.json")).toThrow(Error);
    expect(() => getFileLanguageOrThrow("not_valid.sw.json.txt")).toThrow(Error);
    expect(() => getFileLanguageOrThrow("/home/user/Desktop/not_valid.json")).toThrow(Error);
    expect(() => getFileLanguageOrThrow("C:\\Users\\MyName\\Desktop\\not_valid.json")).toThrow(Error);
  });

  it.each([
    ["valid.sw.json", FileLanguage.JSON],
    ["/home/user/Desktop/valid.sw.json", FileLanguage.JSON],
    ["C:\\Users\\MyName\\Desktop\\valid.sw.json", FileLanguage.JSON],
    ["VALID.SW.JSON", FileLanguage.JSON],
    ["/HOME/USER/DESKTOP/VALID.SW.JSON", FileLanguage.JSON],
    ["C:\\USERS\\MYNAME\\DESKTOP\\VALID.SW.JSON", FileLanguage.JSON],
    ["valid.sw.yml", FileLanguage.YAML],
    ["VALID.SW.YML", FileLanguage.YAML],
    ["/home/user/Desktop/valid.sw.yml", FileLanguage.YAML],
    ["C:\\Users\\MyName\\Desktop\\valid.sw.yml", FileLanguage.YAML],
    ["valid.sw.yaml", FileLanguage.YAML],
    ["/home/user/Desktop/valid.sw.yaml", FileLanguage.YAML],
    ["C:\\Users\\MyName\\Desktop\\valid.sw.yaml", FileLanguage.YAML],
    ["VALID.SW.YAML", FileLanguage.YAML],
    ["/HOME/USER/DESKTOP/VALID.SW.YAML", FileLanguage.YAML],
    ["C:\\USERS\\MYNAME\\DESKTOP\\VALID.SW.YAML", FileLanguage.YAML],
  ])("Checking file language of: %s", (fileName, expectFileLanguage) => {
    expect(getFileLanguageOrThrow(fileName)).toBe(expectFileLanguage);
  });
});
