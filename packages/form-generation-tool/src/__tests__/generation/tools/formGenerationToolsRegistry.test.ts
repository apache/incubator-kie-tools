/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import { lookupFormGenerationTool, registerFormGenerationTool } from "../../../generation/tools";
import { FormGenerationTool, FormStyle } from "../../../generation/types";

describe("formGenerationToolsRegistry tests", () => {
  it("Lookup existing tool - patternfly", () => {
    const tool = lookupFormGenerationTool(FormStyle.PATTERNFLY);

    expect(tool).not.toBeUndefined();

    expect(tool.type).toStrictEqual(FormStyle.PATTERNFLY);
  });

  it("Lookup wrong tool", () => {
    const toolType = "wrong tool type";
    expect(() => lookupFormGenerationTool(toolType)).toThrow(`Unsupported form type "${toolType}"`);
  });

  it("Register tool & lookup", () => {
    const tool: FormGenerationTool = {
      type: "cool new tool",
      generate: jest.fn(),
    };

    registerFormGenerationTool(tool);

    const coolTool = lookupFormGenerationTool(tool.type);
    expect(coolTool).not.toBeUndefined();
    expect(coolTool).toStrictEqual(tool);

    const patternfly = lookupFormGenerationTool(FormStyle.PATTERNFLY);
    expect(patternfly).not.toBeUndefined();
  });
});
