/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import { parseYamlContent } from "@kie-tools/json-yaml-language-service/dist/channel";
import { getStateNameFromOffset, getStateNameOffset } from "./SwfOffsets";

export function getYamlStateNameOffset(args: { content: string; stateName: string }): number | undefined {
  const rootNode = parseYamlContent(args.content);

  return getStateNameOffset({ ...args, rootNode });
}

export function getYamlStateNameFromOffset(args: { content: string; offset: number }): string | undefined {
  const rootNode = parseYamlContent(args.content);

  return getStateNameFromOffset({ ...args, rootNode });
}
