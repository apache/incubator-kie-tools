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
import { TextDocument } from "vscode-languageserver-textdocument";
import {
  CodeLens,
  CompletionItem,
  CompletionItemKind,
  Diagnostic,
  DiagnosticSeverity,
  Position,
  Range,
} from "vscode-languageserver-types";
import { FileLanguage } from "../api";
import * as jsonc from "jsonc-parser";
import { DashbuilderLanguageServiceCodeCompletion } from "./DashbuilderLanguageServiceCodeCompletion";
import { DashbuilderLanguageServiceCodeLenses } from "./DashbuilderLanguageServiceCodeLenses";
import {
  ELsJsonPath,
  ELsNode,
  indentText,
  ShouldCompleteArgs,
  TranslateArgs,
} from "@kie-tools/editor-language-service/dist/channel";
import {
  dump,
  Kind,
  load,
  YAMLAnchorReference,
  YamlMap,
  YAMLMapping,
  YAMLNode,
  YAMLScalar,
  YAMLSequence,
} from "yaml-language-server-parser";
import {
  getLanguageService,
  LanguageSettings,
  SchemaRequestService,
  SettingsState,
  Telemetry,
  WorkspaceContextService,
} from "@kie-tools/yaml-language-server";
import { Connection } from "vscode-languageserver/node";
import { DASHBUILDER_SCHEMA } from "../assets/schemas";
import { CodeCompletionStrategy, ShouldCreateCodelensArgs } from "./types";
import {
  DashbuilderLanguageService,
  DashbuilderLanguageServiceArgs,
  findNodeAtOffset,
  positions_equals,
} from "./DashbuilderLanguageService";

export class DashbuilderYamlLanguageService {
  private readonly ls: DashbuilderLanguageService;
  private readonly codeCompletionStrategy: DashbuilderYamlCodeCompletionStrategy;

  constructor() {
    this.ls = new DashbuilderLanguageService({
      fs: {},
      lang: {
        fileLanguage: FileLanguage.YAML,
        fileMatch: ["*.dash.yaml", "*.dash.yml"],
      },
    });
    this.codeCompletionStrategy = new DashbuilderYamlCodeCompletionStrategy();
  }

  parseContent(content: string): ELsNode | undefined {
    if (!content.trim()) {
      return;
    }

    const ast = load(content);

    // check if the yaml is not valid
    if (ast && ast.errors && ast.errors.length) {
      return;
    }

    return astConvert(ast);
  }

  public async getCompletionItems(args: {
    content: string;
    uri: string;
    cursorPosition: Position;
    cursorWordRange: Range;
  }): Promise<CompletionItem[]> {
    const rootNode = this.parseContent(args.content);
    const doc = TextDocument.create(args.uri, FileLanguage.YAML, 0, args.content);
    const cursorOffset = doc.offsetAt(args.cursorPosition);

    if (shouldNotComplete(args.content.slice(0, cursorOffset))) {
      return [];
    }

    const isCurrentNodeUncompleted = rootNode
      ? isNodeUncompleted({
          ...args,
          rootNode,
          cursorOffset,
        })
      : false;

    if (isCurrentNodeUncompleted) {
      args.cursorPosition = Position.create(args.cursorPosition.line, args.cursorPosition.character - 1);
    }

    return await this.ls.getCompletionItems({
      ...args,
      rootNode,
      codeCompletionStrategy: this.codeCompletionStrategy,
    });
  }

  public async getCodeLenses(args: { content: string; uri: string }): Promise<CodeLens[]> {
    const rootNode = this.parseContent(args.content);
    if (!args.content.trim().length) {
      return DashbuilderLanguageServiceCodeLenses.createNewFile();
    }

    if (!rootNode) {
      return [];
    }
    return [];
  }

  public async getDiagnostics(args: { content: string; uriPath: string }): Promise<Diagnostic[]> {
    if (!args.content.trim()) {
      return [];
    }
    const rootNode = this.parseContent(args.content);
    const loadErrors = !rootNode ? load(args.content).errors : [];
    //check the syntax
    if (loadErrors.length > 0) {
      const error = loadErrors[0];
      const position = Position.create(error.mark.line, error.mark.column);
      // show only the first error because syntax errors are repeated for each line, after the error.
      return [
        {
          message: error.message,
          range: Range.create(position, position),
          severity: DiagnosticSeverity.Error,
        },
      ];
    }

    if (!rootNode) {
      return [];
    }

    // this ensure the document is validated again
    const docVersion = Math.floor(Math.random() * 1000);

    const textDocument = TextDocument.create(
      args.uriPath,
      `dashbuilder-${FileLanguage.YAML}`,
      docVersion,
      args.content
    );

    return await this.getSchemaDiagnostics(textDocument, ["*.dash.yaml", "*.dash.yml", "*.dash.json"]);
  }

  private async getSchemaDiagnostics(textDocument: TextDocument, fileMatch: string[]): Promise<Diagnostic[]> {
    const schemaRequestService: SchemaRequestService = async (uri: string) => {
      if (uri === DASHBUILDER_SCHEMA.$id) {
        return Promise.resolve(JSON.stringify(DASHBUILDER_SCHEMA));
      } else {
        throw new Error(`Unable to load schema from '${uri}'`);
      }
    };
    const workspaceContext: WorkspaceContextService = {
      resolveRelativePath: (_relativePath: string, _resource: string) => {
        return "";
      },
    };

    const connection = {} as Connection;
    connection.onRequest = () => null;
    const telemetry = new Telemetry(connection);

    const yamlSettings = { yamlFormatterSettings: { enable: false } } as SettingsState;
    const yamlLanguageSettings: LanguageSettings = {
      validate: true,
      completion: true,
      format: false,
      hover: false,
      isKubernetes: false,
      schemas: [{ fileMatch, uri: DASHBUILDER_SCHEMA.$id }],
    };
    const yamlLs = getLanguageService(schemaRequestService, workspaceContext, connection, telemetry, yamlSettings);
    yamlLs.configure(yamlLanguageSettings);
    return yamlLs.doValidation(textDocument, false);
  }

  public dispose() {
    return this.ls.dispose();
  }
}

/**
 * Check if a node at a position is uncompleted.
 * eg. "refName: 🎯"
 *
 * @param args -
 * @returns true if the node is uncompleted, false otherwise.
 */
export const isNodeUncompleted = (args: {
  content: string;
  uri: string;
  rootNode: ELsNode;
  cursorOffset: number;
}): boolean => {
  if (args.content.slice(args.cursorOffset - 1, args.cursorOffset) !== " ") {
    return false;
  }

  const nodeAtPrevOffset = findNodeAtOffset(args.rootNode, args.cursorOffset - 1, true);

  if (!nodeAtPrevOffset) {
    return false;
  }

  return nodeAtPrevOffset.offset + nodeAtPrevOffset.length === args.cursorOffset - 1;
};

/**
 * Check if a string should not be completed.
 * Gets the last line of the content, check if we are after the first ":" without a space OR after a "-" without a space.
 * Eg. https://regex101.com/r/AaUWZg/1
 * @param content the content of the file until the point where the completion should be triggered.
 * @returns
 */
function shouldNotComplete(content: string): boolean {
  return /(^|\n)\s*(([^:]+:)|(-))$/.test(content);
}

const astConvert = (node: YAMLNode, parentNode?: ELsNode): ELsNode => {
  const convertedNode: ELsNode = {
    type: "object",
    offset: node.startPosition,
    length: node.endPosition - node.startPosition,
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
    convertedNode.children = [
      astConvert(yamlMapping.key, convertedNode),
      ...(convertedNode.value ? [astConvert(yamlMapping.value, convertedNode)] : []),
    ];
    convertedNode.type = "property";
    convertedNode.colonOffset = yamlMapping.key.endPosition;
  } else if (node.kind === Kind.SEQ) {
    convertedNode.children = (node as YAMLSequence).items
      .filter((item) => item)
      .map((item) => astConvert(item, convertedNode));
    convertedNode.type = "array";
  } else if (node.kind === Kind.ANCHOR_REF || node.kind === Kind.INCLUDE_REF) {
    convertedNode.value = (node as YAMLAnchorReference).value;
    convertedNode.type = "object";
  }

  return convertedNode;
};

export class DashbuilderYamlCodeCompletionStrategy implements CodeCompletionStrategy {
  public translate(args: TranslateArgs): string {
    const completionDump = dump(args.completion, {}).slice(2, -1).trim();
    if (["{}", "[]"].includes(completionDump) || args.completionItemKind === CompletionItemKind.Text) {
      return completionDump;
    }
    const skipFirstLineIndent = args.completionItemKind !== CompletionItemKind.Module;
    const completionItemNewLine = args.completionItemKind === CompletionItemKind.Module ? "\n" : "";
    const completionText = completionItemNewLine + indentText(completionDump, 2, " ", skipFirstLineIndent);
    return ([CompletionItemKind.Interface, CompletionItemKind.Reference] as CompletionItemKind[]).includes(
      args.completionItemKind
    ) && positions_equals(args.overwriteRange?.start ?? null, args.currentNodeRange?.start ?? null)
      ? `- ${completionText}\n`
      : completionText;
  }

  public formatLabel(_label: string, _completionItemKind: CompletionItemKind): string {
    return "";
  }

  public getStartNodeValuePosition(_document: TextDocument, _node: ELsNode): Position | undefined {
    return undefined;
  }

  public shouldComplete(_args: ShouldCompleteArgs): boolean {
    return true;
  }

  public shouldCreateCodelens(_args: ShouldCreateCodelensArgs): boolean {
    return true;
  }
}
