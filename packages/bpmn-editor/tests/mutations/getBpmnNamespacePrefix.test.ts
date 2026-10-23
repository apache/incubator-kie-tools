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

import { BPMN20_MODEL_NAMESPACE, getBpmnNamespacePrefix } from "../../src/mutations/_dataMapping";

describe("getBpmnNamespacePrefix", () => {
  it("returns empty string when definitions is undefined", () => {
    expect(getBpmnNamespacePrefix(undefined)).toBe("");
  });

  it("returns empty string when definitions has no namespace entries", () => {
    const definitions = { "@_id": "def1" } as any;
    expect(getBpmnNamespacePrefix(definitions)).toBe("");
  });

  it("returns empty string when BPMN model namespace is bound to the default xmlns (no prefix)", () => {
    // Files where xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL" — no prefix needed
    const definitions = {
      "@_id": "def1",
      "@_xmlns": BPMN20_MODEL_NAMESPACE,
    } as any;
    expect(getBpmnNamespacePrefix(definitions)).toBe("");
  });

  it("returns 'bpmn2:' when BPMN model namespace is bound to the bpmn2 prefix", () => {
    // Files edited with older editors that use bpmn2: as namespace prefix
    const definitions = {
      "@_id": "def1",
      "@_xmlns:bpmn2": BPMN20_MODEL_NAMESPACE,
    } as any;
    expect(getBpmnNamespacePrefix(definitions)).toBe("bpmn2:");
  });

  it("returns 'bpmn:' when BPMN model namespace is bound to a custom 'bpmn' prefix", () => {
    const definitions = {
      "@_id": "def1",
      "@_xmlns:bpmn": BPMN20_MODEL_NAMESPACE,
    } as any;
    expect(getBpmnNamespacePrefix(definitions)).toBe("bpmn:");
  });

  it("returns empty string when a different namespace is bound to xmlns but not the BPMN model namespace", () => {
    const definitions = {
      "@_id": "def1",
      "@_xmlns": "http://some.other.namespace",
      "@_xmlns:bpmn2": "http://also.other.namespace",
    } as any;
    expect(getBpmnNamespacePrefix(definitions)).toBe("");
  });

  it("ignores non-xmlns keys that happen to have the BPMN model namespace as value", () => {
    const definitions = {
      "@_id": "def1",
      "@_targetNamespace": BPMN20_MODEL_NAMESPACE,
    } as any;
    expect(getBpmnNamespacePrefix(definitions)).toBe("");
  });

  it("returns the first matching prefix when multiple xmlns entries are present", () => {
    // In practice only one will match, but the function returns the first hit
    const definitions = {
      "@_id": "def1",
      "@_xmlns:bpmn2": BPMN20_MODEL_NAMESPACE,
      "@_xmlns:other": "http://other.ns",
    } as any;
    expect(getBpmnNamespacePrefix(definitions)).toBe("bpmn2:");
  });
});
