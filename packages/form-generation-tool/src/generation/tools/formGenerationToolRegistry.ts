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

import { PatternflyFormGenerationTool } from "./uniforms/patternfly/PatternflyFormGenerationTool";
import { FormGenerationTool } from "../types";
import { Bootstrap4FormGenerationTool } from "./uniforms/bootstrap4/Bootstrap4FormGenerationTool";

const toolsRegistry: Map<string, FormGenerationTool> = new Map<string, FormGenerationTool>();

export function registerFormGenerationTool(formGenerationTool: FormGenerationTool) {
  toolsRegistry.set(formGenerationTool.type, formGenerationTool);
}

registerFormGenerationTool(new PatternflyFormGenerationTool());
registerFormGenerationTool(new Bootstrap4FormGenerationTool());

export function lookupFormGenerationTool(type: string): FormGenerationTool {
  const tool = toolsRegistry.get(type);
  if (tool) {
    return tool;
  }
  throw new Error(`Unsupported form type "${type}"`);
}
