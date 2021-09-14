/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
  isConfigValid,
  isHostValid,
  isNamespaceValid,
  isTokenValid,
} from "../../../editor/DmnDevSandbox/DmnDevSandboxConnectionConfig";

describe("Validation functions", () => {
  test("Namespace should be valid", () => {
    expect(isNamespaceValid("namespace")).toBe(true);
  });

  test("Namespace should not be valid", () => {
    expect(isNamespaceValid("")).toBe(false);
    expect(isNamespaceValid("     ")).toBe(false);
  });

  test("Host should be valid", () => {
    expect(isHostValid("http://dmn-dev-sandbox.com")).toBe(true);
    expect(isHostValid("https://dmn-dev-sandbox.com")).toBe(true);
  });

  test("Host should not be valid", () => {
    expect(isHostValid("")).toBe(false);
    expect(isHostValid("     ")).toBe(false);
    expect(isHostValid("not-a-host")).toBe(false);
    expect(isHostValid("=http://dmn-dev-sandbox.com")).toBe(false);
  });

  test("Token should be valid", () => {
    expect(isTokenValid("token")).toBe(true);
  });

  test("Token should not be valid", () => {
    expect(isTokenValid("")).toBe(false);
    expect(isTokenValid("     ")).toBe(false);
  });

  test("Config should be valid", () => {
    expect(isConfigValid({ namespace: "namespace", host: "https://dmn-dev-sandbox.com", token: "token" })).toBe(true);
  });

  test("Config should not be valid", () => {
    expect(isConfigValid({ namespace: "", host: "", token: "" })).toBe(false);
    expect(isConfigValid({ namespace: "namespace", host: "https://dmn-dev-sandbox.com", token: "" })).toBe(false);
    expect(isConfigValid({ namespace: "", host: "https://dmn-dev-sandbox.com", token: "token" })).toBe(false);
    expect(isConfigValid({ namespace: "namespace", host: "", token: "token" })).toBe(false);
  });
});
