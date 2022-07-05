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
  Kind,
  load,
  YAMLAnchorReference,
  YamlMap,
  YAMLMapping,
  YAMLNode,
  YAMLScalar,
  YAMLSequence,
} from "yaml-language-server-parser";
import { FileLanguage } from "../editor";
import { SwfLanguageService, SwfLanguageServiceArgs, SwfLSNode } from "./SwfLanguageService";

export class SwfYamlLanguageService extends SwfLanguageService {
  fileLanguage = FileLanguage.YAML;
  protected fileMatch = ["*.sw.yaml", "*.sw.yml"];

  constructor(args: SwfLanguageServiceArgs) {
    super(args);
  }

  parseContent(content: string): SwfLSNode | undefined {
    const ast = load(content);

    // check if the yaml is not valid
    if (ast && ast.errors && ast.errors.length) {
      throw new Error(ast.errors[0].message);
    }

    return astConvert(ast);
  }
}

const astConvert = (node: YAMLNode, parentNode?: SwfLSNode): SwfLSNode => {
  const convertedNode: SwfLSNode = {
    type: "object",
    offset: node.startPosition,
    length: node.endPosition - node.startPosition,
    colonOffset: node.endPosition,
    parent: parentNode,
  };

  if (node.kind === Kind.SCALAR) {
    convertedNode.value = (node as YAMLScalar).value;
    convertedNode.type = "string";
  } else if (node.kind === Kind.MAP) {
    const yamlMap = node as YamlMap;
    convertedNode.value = yamlMap.value;
    convertedNode.children = yamlMap.mappings.map((mapping) => astConvert(mapping, convertedNode));
    convertedNode.type = "object";
  } else if (node.kind === Kind.MAPPING) {
    const yamlMapping = node as YAMLMapping;
    convertedNode.value = yamlMapping.value;
    convertedNode.children = [astConvert(yamlMapping.key, convertedNode), astConvert(yamlMapping.value, convertedNode)];
    convertedNode.type = "property";
  } else if (node.kind === Kind.SEQ) {
    convertedNode.children = (node as YAMLSequence).items.map((item) => astConvert(item, convertedNode));
    convertedNode.type = "array";
  } else if (node.kind === Kind.ANCHOR_REF || node.kind === Kind.INCLUDE_REF) {
    convertedNode.value = (node as YAMLAnchorReference).value;
    convertedNode.type = "object";
  }

  return convertedNode;
};
