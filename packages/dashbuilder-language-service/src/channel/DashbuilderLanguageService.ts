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
  EditorLanguageService,
  EditorLanguageServiceArgs,
  ELsCompletionsMap,
  ELsJsonPath,
  ELsNode,
  indentText,
  ShouldCompleteArgs,
  TranslateArgs,
} from "@kie-tools/editor-language-service/dist/channel";
import {
  getLanguageService,
  LanguageSettings,
  SchemaRequestService,
  SettingsState,
  Telemetry,
  WorkspaceContextService,
} from "@kie-tools/yaml-language-server";
import * as jsonc from "jsonc-parser";
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
import { Connection } from "vscode-languageserver/node";
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
import { FileLanguage } from "../api";
import { DASHBUILDER_SCHEMA } from "../assets/schemas";
import {
  DashbuilderLanguageServiceCodeCompletion,
  DashbuilderLanguageServiceCodeCompletionFunctionsArgs,
} from "./DashbuilderLanguageServiceCodeCompletion";
import { DashbuilderLanguageServiceCodeLenses } from "./DashbuilderLanguageServiceCodeLenses";
import { CodeCompletionStrategy, ShouldCreateCodelensArgs } from "./types";

export type DashbuilderLanguageServiceArgs = EditorLanguageServiceArgs;

export class DashbuilderLanguageService {
  private readonly els: EditorLanguageService;

  constructor(private readonly args: DashbuilderLanguageServiceArgs) {
    this.els = new EditorLanguageService(this.args);
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
    rootNode: ELsNode | undefined;
    codeCompletionStrategy: CodeCompletionStrategy;
  }): Promise<CompletionItem[]> {
    return this.els.getCompletionItems({
      ...args,
      completions,
    });
  }

  public async getCodeLenses(args: {
    content: string;
    uri: string;
    rootNode: ELsNode | undefined;
    codeCompletionStrategy: CodeCompletionStrategy;
  }): Promise<CodeLens[]> {
    return this.els.getCodeLenses({
      ...args,
      codeLenses: DashbuilderLanguageServiceCodeLenses,
    });
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
    // empty for now
  }
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

export function findNodeAtOffset(root: ELsNode, offset: number, includeRightBound?: boolean): ELsNode | undefined {
  return jsonc.findNodeAtOffset(root as jsonc.Node, offset, includeRightBound) as ELsNode;
}

export function getNodePath(node: ELsNode): ELsJsonPath {
  return jsonc.getNodePath(node as jsonc.Node);
}

const completions: ELsCompletionsMap<DashbuilderLanguageServiceCodeCompletionFunctionsArgs> = new Map([
  [null, DashbuilderLanguageServiceCodeCompletion.getEmptyFileCodeCompletions],
]);

export class DashbuilderCodeCompletionStrategy implements CodeCompletionStrategy {
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

/**
 * Test if position `a` equals position `b`.
 * This function is compatible with https://microsoft.github.io/monaco-editor/api/classes/monaco.Position.html#equals-1
 *
 * @param a -
 * @param b -
 * @returns true if the positions are equal, false otherwise
 */
export const positions_equals = (a: Position | null, b: Position | null): boolean =>
  a?.line === b?.line && a?.character == b?.character;
