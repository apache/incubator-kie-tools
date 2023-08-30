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

import * as jsonc from "jsonc-parser";
import { load } from "yaml-language-server-parser";
import { FileLanguage } from "../api";
import { ELsNode } from "./types";

/**
 * Detect the format of a node's content.
 * Note: for strings in double quotes and boolean values it's not able to distinguish between JSON and YAML. JSON will be returned in those cases.
 *
 * @param content the content
 * @param node the node
 * @returns the FileLanguage, undefined if unrecognized
 */
export function getNodeFormat(content: string, node: ELsNode): FileLanguage | undefined {
  const nodeContent = content.slice(node.offset, node.offset + node.length);

  if (jsonc.parseTree(nodeContent) !== undefined) {
    return FileLanguage.JSON;
  }

  const yamlAST = load(nodeContent);

  if (node.type === "array" && node.parent && nodeContent.slice(0, 1) == "-") {
    // if the node is an array and starts with "-", use the parent to have a valid YAML string
    return getNodeFormat(content, node.parent);
  }

  if (yamlAST && !yamlAST.errors.length) {
    return FileLanguage.YAML;
  }

  return;
}
