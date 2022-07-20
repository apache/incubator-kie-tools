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
import { CodeLens, CompletionItem, Position, Range } from "vscode-languageserver-types";
import { SwfLanguageService, SwfLanguageServiceArgs } from "./SwfLanguageService";
import { SwfLsNode } from "./types";
import { FileLanguage } from "../api";

export class SwfYamlLanguageService {
  private readonly ls: SwfLanguageService;

  constructor(args: Omit<SwfLanguageServiceArgs, "lang">) {
    this.ls = new SwfLanguageService({
      ...args,
      lang: {
        fileLanguage: FileLanguage.YAML,
        fileMatch: ["*.sw.yaml", "*.sw.yml"],
      },
    });
  }

  parseContent(content: string): SwfLsNode | undefined {
    const ast = load(content);

    // check if the yaml is not valid
    if (ast && ast.errors && ast.errors.length) {
      throw new Error(ast.errors[0].message);
    }

    return astConvert(ast);
  }

  public getCompletionItems(args: {
    content: string;
    uri: string;
    cursorPosition: Position;
    cursorWordRange: Range;
  }): Promise<CompletionItem[]> {
    return this.ls.getCompletionItems({ ...args, rootNode: this.parseContent(args.content) });
  }

  public async getCodeLenses(args: { content: string; uri: string }): Promise<CodeLens[]> {
    return this.ls.getCodeLenses({ ...args, rootNode: this.parseContent(args.content) });
  }

  public async getDiagnostics(args: { content: string; uriPath: string }) {
    return this.ls.getDiagnostics(args);
  }

  public dispose() {
    return this.ls.dispose();
  }
}

const astConvert = (node: YAMLNode, parentNode?: SwfLsNode): SwfLsNode => {
  const convertedNode: SwfLsNode = {
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
