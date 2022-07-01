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
  CodeLens,
  CompletionItem,
  CompletionItemKind,
  InsertTextFormat,
  Position,
  Range,
} from "vscode-languageserver-types";
import * as jsonc from "jsonc-parser";
import { posix as posixPath } from "path";
import {
  SwfServiceCatalogFunction,
  SwfServiceCatalogFunctionSourceType,
  SwfServiceCatalogService,
  SwfServiceCatalogServiceSourceType,
} from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import { SwfLanguageServiceCommandArgs, SwfLanguageServiceCommandExecution } from "../api";
import * as swfModelQueries from "./modelQueries";
import { Specification } from "@severlessworkflow/sdk-typescript";
import { SW_SPEC_WORKFLOW_SCHEMA } from "../schemas";
import { getLanguageService, TextDocument } from "vscode-json-languageservice";
import { FileLanguage } from "../editor";

// SwfJSONPath, SwfLSNode, SwfLSNodeType need to be compatible with jsonc types
export declare type SwfJSONPath = (string | number)[];
export declare type SwfLSNodeType = "object" | "array" | "property" | "string" | "number" | "boolean" | "null";
export type SwfLSNode = {
  type: SwfLSNodeType;
  value?: any;
  offset: number;
  length: number;
  colonOffset?: number;
  parent?: SwfLSNode;
  children?: SwfLSNode[];
};

export type SwfLanguageServiceConfig = {
  shouldConfigureServiceRegistries: () => boolean; //TODO: See https://issues.redhat.com/browse/KOGITO-7107
  shouldServiceRegistriesLogIn: () => boolean; //TODO: See https://issues.redhat.com/browse/KOGITO-7107
  canRefreshServices: () => boolean; //TODO: See https://issues.redhat.com/browse/KOGITO-7107
  getSpecsDirPosixPaths: (
    textDocument: TextDocument
  ) => Promise<{ specsDirRelativePosixPath: string; specsDirAbsolutePosixPath: string }>;
  shouldDisplayServiceRegistriesIntegration: () => Promise<boolean>;
  shouldReferenceServiceRegistryFunctionsWithUrls: () => Promise<boolean>;
};

export type SwfLanguageServiceArgs = {
  fs: {};
  serviceCatalog: {
    global: {
      getServices: () => Promise<SwfServiceCatalogService[]>;
    };
    relative: {
      getServices: (textDocument: TextDocument) => Promise<SwfServiceCatalogService[]>;
    };
    getServiceFileNameFromSwfServiceCatalogServiceId: (
      registryName: string,
      swfServiceCatalogServiceId: string
    ) => Promise<string>;
  };
  config: SwfLanguageServiceConfig;
};

export abstract class SwfLanguageService {
  protected abstract fileMatch: string[];
  protected abstract fileLanguage: FileLanguage;

  constructor(private readonly args: SwfLanguageServiceArgs) {}

  public abstract parseContent(content: string): SwfLSNode | undefined;

  public async getCompletionItems(args: {
    content: string;
    uri: string;
    cursorPosition: Position;
    cursorWordRange: Range;
  }): Promise<CompletionItem[]> {
    const doc = TextDocument.create(args.uri, this.fileLanguage, 0, args.content);
    const rootNode = this.parseContent(args.content);
    if (!rootNode) {
      return [];
    }

    const cursorOffset = doc.offsetAt(args.cursorPosition);

    const currentNode = findNodeAtOffset(rootNode, cursorOffset);
    if (!currentNode) {
      return [];
    }

    const currentNodePosition = {
      start: doc.positionAt(currentNode.offset),
      end: doc.positionAt(currentNode.offset + currentNode.length),
    };

    const overwriteRange = ["string", "number", "boolean", "null"].includes(currentNode?.type)
      ? Range.create(currentNodePosition.start, currentNodePosition.end)
      : args.cursorWordRange;

    const swfCompletionItemServiceCatalogServices = await Promise.all(
      [
        ...(await this.args.serviceCatalog.global.getServices()),
        ...(await this.args.serviceCatalog.relative.getServices(doc)),
      ].map(async (service) => ({
        ...service,
        functions: await Promise.all(
          service.functions.map(async (func) => ({
            ...func,
            operation: await this.getSwfCompletionItemServiceCatalogFunctionOperation(service, func, doc),
          }))
        ),
      }))
    );

    const root = this.parseContent(args.content);
    const nodeAtOffset = findNodeAtOffset(root!, cursorOffset);

    const result = await Promise.all(
      Array.from(completions.entries())
        .filter(([path, _]) => this.matchNodeWithLocation(root, nodeAtOffset, path))
        .map(([_, completionItemsDelegate]) => {
          return completionItemsDelegate({
            document: doc,
            cursorPosition: args.cursorPosition,
            currentNode,
            currentNodePosition,
            rootNode,
            overwriteRange,
            swfCompletionItemServiceCatalogServices,
            langServiceConfig: this.args.config,
          });
        })
    );

    return Promise.resolve(result.flat());
  }

  public async getDiagnostics(args: { content: string; uriPath: string }) {
    const textDocument = TextDocument.create(args.uriPath, `serverless-workflow-${this.fileLanguage}`, 1, args.content);

    const schemaUri = "https://serverlessworkflow.io/schemas/0.8/workflow.json";

    const jsonLanguageService = getLanguageService({
      schemaRequestService: (uri) => {
        if (uri === schemaUri) {
          return Promise.resolve(JSON.stringify(SW_SPEC_WORKFLOW_SCHEMA));
        }
        return Promise.reject(`Unabled to load schema at ${uri}`);
      },
    });

    jsonLanguageService.configure({
      allowComments: false,
      schemas: [{ fileMatch: this.fileMatch, uri: schemaUri }],
    });

    const jsonDocument = jsonLanguageService.parseJSONDocument(textDocument);

    return await jsonLanguageService.doValidation(textDocument, jsonDocument);
  }

  public async getCodeLenses(args: { content: string; uri: string }): Promise<CodeLens[]> {
    const document = TextDocument.create(args.uri, this.fileLanguage, 0, args.content);

    const rootNode = this.parseContent(document.getText());
    if (!rootNode) {
      return [];
    }

    const addFunction = this.createCodeLenses({
      document,
      rootNode,
      jsonPath: ["functions"],
      positionLensAt: "begin",
      commandDelegates: ({ node }) => {
        if (node.type !== "array") {
          return [];
        }

        const newCursorPosition = document.positionAt(node.offset + 1);

        return [
          {
            name: "swf.ls.commands.OpenFunctionsCompletionItems",
            title: "+ Add function...",
            args: [
              { newCursorPosition } as SwfLanguageServiceCommandArgs["swf.ls.commands.OpenFunctionsCompletionItems"],
            ],
          },
        ];
      },
    });

    const setupServiceRegistries = this.createCodeLenses({
      document,
      rootNode,
      jsonPath: ["functions"],
      positionLensAt: "begin",
      commandDelegates: ({ position, node }) => {
        if (node.type !== "array") {
          return [];
        }

        if (!this.args.config.shouldConfigureServiceRegistries()) {
          return [];
        }

        return [
          {
            name: "swf.ls.commands.OpenServiceRegistriesConfig",
            title: "↪ Setup Service Registries...",
            args: [{ position } as SwfLanguageServiceCommandArgs["swf.ls.commands.OpenServiceRegistriesConfig"]],
          },
        ];
      },
    });

    const logInServiceRegistries = this.createCodeLenses({
      document,
      rootNode,
      jsonPath: ["functions"],
      positionLensAt: "begin",
      commandDelegates: ({ position, node }) => {
        if (node.type !== "array") {
          return [];
        }

        if (this.args.config.shouldConfigureServiceRegistries()) {
          return [];
        }

        if (!this.args.config.shouldServiceRegistriesLogIn()) {
          return [];
        }

        return [
          {
            name: "swf.ls.commands.LogInServiceRegistries",
            title: "↪ Log in Service Registries...",
            args: [{ position } as SwfLanguageServiceCommandArgs["swf.ls.commands.LogInServiceRegistries"]],
          },
        ];
      },
    });

    const refreshServiceRegistries = this.createCodeLenses({
      document,
      rootNode,
      jsonPath: ["functions"],
      positionLensAt: "begin",
      commandDelegates: ({ position, node }) => {
        if (node.type !== "array") {
          return [];
        }

        if (this.args.config.shouldConfigureServiceRegistries()) {
          return [];
        }

        if (!this.args.config.canRefreshServices()) {
          return [];
        }

        return [
          {
            name: "swf.ls.commands.RefreshServiceRegistries",
            title: "↺ Refresh Service Registries...",
            args: [{ position } as SwfLanguageServiceCommandArgs["swf.ls.commands.RefreshServiceRegistries"]],
          },
        ];
      },
    });

    const displayRhhccIntegration = await this.args.config.shouldDisplayServiceRegistriesIntegration();

    return [
      ...(displayRhhccIntegration ? setupServiceRegistries : []),
      ...(displayRhhccIntegration ? logInServiceRegistries : []),
      ...(displayRhhccIntegration ? refreshServiceRegistries : []),
      ...addFunction,
    ];
  }

  public dispose() {
    // empty for now
  }

  private async getSwfCompletionItemServiceCatalogFunctionOperation(
    containingService: SwfServiceCatalogService,
    func: SwfServiceCatalogFunction,
    document: TextDocument
  ): Promise<string> {
    const { specsDirRelativePosixPath } = await this.args.config.getSpecsDirPosixPaths(document);

    if (func.source.type === SwfServiceCatalogFunctionSourceType.LOCAL_FS) {
      const serviceFileName = posixPath.basename(func.source.serviceFileAbsolutePath);
      const serviceFileRelativePosixPath = posixPath.join(specsDirRelativePosixPath, serviceFileName);
      return `${serviceFileRelativePosixPath}#${func.name}`;
    }

    //
    else if (
      (await this.args.config.shouldReferenceServiceRegistryFunctionsWithUrls()) &&
      containingService.source.type === SwfServiceCatalogServiceSourceType.SERVICE_REGISTRY &&
      func.source.type === SwfServiceCatalogFunctionSourceType.SERVICE_REGISTRY
    ) {
      return `${containingService.source.url}#${func.name}`;
    }

    //
    else if (
      containingService.source.type === SwfServiceCatalogServiceSourceType.SERVICE_REGISTRY &&
      func.source.type === SwfServiceCatalogFunctionSourceType.SERVICE_REGISTRY
    ) {
      const serviceFileName = await this.args.serviceCatalog.getServiceFileNameFromSwfServiceCatalogServiceId(
        containingService.source.registry,
        containingService.source.id
      );
      const serviceFileRelativePosixPath = posixPath.join(specsDirRelativePosixPath, serviceFileName);
      return `${serviceFileRelativePosixPath}#${func.name}`;
    }

    //
    else {
      throw new Error("Unknown Service Catalog function source type");
    }
  }

  /**
   * Check if a Node is in Location.
   *
   * @param root root node
   * @param node the Node to check
   * @param path the location to verify
   * @returns true if the node is in the location, false otherwise
   */
  public matchNodeWithLocation(root: SwfLSNode | undefined, node: SwfLSNode | undefined, path: SwfJSONPath): boolean {
    if (!root || !node || !path || !path.length) {
      return false;
    }

    const nodesAtLocation = findNodesAtLocation(root, path);

    if (nodesAtLocation.some((currentNode) => currentNode === node)) {
      return true;
    } else if (path[path.length - 1] === "*" && !nodesAtLocation.length) {
      return this.matchNodeWithLocation(root, node, path.slice(0, -1));
    }

    return false;
  }

  public createCodeLenses(args: {
    document: TextDocument;
    rootNode: SwfLSNode;
    jsonPath: SwfJSONPath;
    commandDelegates: (args: {
      position: Position;
      node: SwfLSNode;
    }) => ({ title: string } & SwfLanguageServiceCommandExecution<any>)[];
    positionLensAt: "begin" | "end";
  }): CodeLens[] {
    const nodes = findNodesAtLocation(args.rootNode, args.jsonPath);
    const codeLenses = nodes.flatMap((node) => {
      // Only position at the end if the type is object or array and has at least one child.
      const position =
        args.positionLensAt === "end" &&
        (node.type === "object" || node.type === "array") &&
        (node.children?.length ?? 0) > 0
          ? args.document.positionAt(node.offset + node.length)
          : args.document.positionAt(node.offset);

      return args.commandDelegates({ position, node }).map((command) => ({
        command: {
          command: command.name,
          title: command.title,
          arguments: command.args,
        },
        range: {
          start: position,
          end: position,
        },
      }));
    });

    return codeLenses;
  }
}

type SwfCompletionItemServiceCatalogFunction = SwfServiceCatalogFunction & { operation: string };
type SwfCompletionItemServiceCatalogService = Omit<SwfServiceCatalogService, "functions"> & {
  functions: SwfCompletionItemServiceCatalogFunction[];
};

const completions = new Map<
  SwfJSONPath,
  (args: {
    swfCompletionItemServiceCatalogServices: SwfCompletionItemServiceCatalogService[];
    document: TextDocument;
    cursorPosition: Position;
    currentNode: SwfLSNode;
    overwriteRange: Range;
    currentNodePosition: { start: Position; end: Position };
    rootNode: SwfLSNode;
    langServiceConfig: SwfLanguageServiceConfig;
  }) => Promise<CompletionItem[]>
>([
  [
    ["functions", "*"],
    async ({
      currentNode,
      rootNode,
      overwriteRange,
      swfCompletionItemServiceCatalogServices,
      document,
      langServiceConfig,
    }) => {
      const separator = currentNode.type === "object" ? "," : "";
      const existingFunctionOperations = swfModelQueries.getFunctions(rootNode).map((f) => f.operation);

      const specsDir = await langServiceConfig.getSpecsDirPosixPaths(document);

      const result = swfCompletionItemServiceCatalogServices.flatMap((swfServiceCatalogService) =>
        swfServiceCatalogService.functions
          .filter((swfServiceCatalogFunc) => !existingFunctionOperations.includes(swfServiceCatalogFunc.operation))
          .map((swfServiceCatalogFunc) => {
            const swfFunction: Omit<Specification.Function, "normalize"> = {
              name: `$\{1:${swfServiceCatalogFunc.name}}`,
              operation: swfServiceCatalogFunc.operation,
              type: swfServiceCatalogFunc.type,
            };

            const command: SwfLanguageServiceCommandExecution<"swf.ls.commands.ImportFunctionFromCompletionItem"> = {
              name: "swf.ls.commands.ImportFunctionFromCompletionItem",
              args: {
                containingService: swfServiceCatalogService,
                documentUri: document.uri,
              },
            };

            return {
              kind:
                swfServiceCatalogFunc.source.type === SwfServiceCatalogFunctionSourceType.SERVICE_REGISTRY
                  ? CompletionItemKind.Interface
                  : CompletionItemKind.Reference,
              label: toCompletionItemLabelPrefix(swfServiceCatalogFunc, specsDir.specsDirRelativePosixPath),
              detail:
                swfServiceCatalogService.source.type === SwfServiceCatalogServiceSourceType.SERVICE_REGISTRY
                  ? swfServiceCatalogService.source.url
                  : swfServiceCatalogFunc.operation,
              textEdit: {
                newText: JSON.stringify(swfFunction, null, 2) + separator,
                range: overwriteRange,
              },
              snippet: true,
              insertTextFormat: InsertTextFormat.Snippet,
              command: {
                command: command.name,
                title: "Import function from completion item",
                arguments: [command.args],
              },
            };
          })
      );
      return Promise.resolve(result);
    },
  ],
  [
    ["functions", "*", "operation"],
    ({ currentNode, rootNode, overwriteRange, swfCompletionItemServiceCatalogServices }) => {
      if (!currentNode.parent?.parent) {
        return Promise.resolve([]);
      }

      // As "rest" is the default, if the value is undefined, it's a rest function too.
      const isRestFunction = (findNodeAtLocation(currentNode.parent.parent, ["type"])?.value ?? "rest") === "rest";
      if (!isRestFunction) {
        return Promise.resolve([]);
      }

      const existingFunctionOperations = swfModelQueries.getFunctions(rootNode).map((f) => f.operation);

      const result = swfCompletionItemServiceCatalogServices
        .flatMap((s) => s.functions)
        .filter((swfServiceCatalogFunc) => !existingFunctionOperations.includes(swfServiceCatalogFunc.operation))
        .map((swfServiceCatalogFunc) => {
          return {
            kind:
              swfServiceCatalogFunc.source.type === SwfServiceCatalogFunctionSourceType.SERVICE_REGISTRY
                ? CompletionItemKind.Function
                : CompletionItemKind.Folder,
            label: `"${swfServiceCatalogFunc.operation}"`,
            detail: `"${swfServiceCatalogFunc.operation}"`,
            filterText: `"${swfServiceCatalogFunc.operation}"`,
            textEdit: {
              newText: `"${swfServiceCatalogFunc.operation}"`,
              range: overwriteRange,
            },
            insertTextFormat: InsertTextFormat.Snippet,
          };
        });
      return Promise.resolve(result);
    },
  ],
  [
    ["states", "*", "actions", "*", "functionRef"],
    ({ overwriteRange, currentNode, rootNode, swfCompletionItemServiceCatalogServices }) => {
      if (currentNode.type !== "property") {
        console.debug("Cannot autocomplete: functionRef should be a property.");
        return Promise.resolve([]);
      }

      const result = swfModelQueries.getFunctions(rootNode).flatMap((swfFunction) => {
        const swfServiceCatalogFunc = swfCompletionItemServiceCatalogServices
          .flatMap((f) => f.functions)
          .filter((f) => f.operation === swfFunction.operation)
          .pop()!;
        if (!swfServiceCatalogFunc) {
          return [];
        }

        let argIndex = 1;
        const swfFunctionRefArgs: Record<string, string> = {};
        Object.keys(swfServiceCatalogFunc.arguments).forEach((argName) => {
          swfFunctionRefArgs[argName] = `$\{${argIndex++}:}`;
        });

        const swfFunctionRef: Omit<Specification.Functionref, "normalize"> = {
          refName: swfFunction.name,
          arguments: swfFunctionRefArgs,
        };

        return [
          {
            kind: CompletionItemKind.Module,
            label: `${swfFunctionRef.refName}`,
            sortText: `${swfFunctionRef.refName}`,
            detail: `${swfServiceCatalogFunc.operation}`,
            textEdit: {
              newText: JSON.stringify(swfFunctionRef, null, 2),
              range: overwriteRange,
            },
            insertTextFormat: InsertTextFormat.Snippet,
          },
        ];
      });

      return Promise.resolve(result);
    },
  ],
  [
    ["states", "*", "actions", "*", "functionRef", "refName"],
    ({ overwriteRange, rootNode }) => {
      const result = swfModelQueries.getFunctions(rootNode).flatMap((swfFunction) => {
        return [
          {
            kind: CompletionItemKind.Value,
            label: `"${swfFunction.name}"`,
            sortText: `"${swfFunction.name}"`,
            detail: `"${swfFunction.name}"`,
            filterText: `"${swfFunction.name}"`,
            textEdit: {
              newText: `"${swfFunction.name}"`,
              range: overwriteRange,
            },
            insertTextFormat: InsertTextFormat.Snippet,
          },
        ];
      });
      return Promise.resolve(result);
    },
  ],
  [
    ["states", "*", "actions", "*", "functionRef", "arguments"],
    ({ overwriteRange, currentNode, rootNode, swfCompletionItemServiceCatalogServices }) => {
      if (currentNode.type !== "property") {
        console.debug("Cannot autocomplete: functionRef should be a property.");
        return Promise.resolve([]);
      }

      if (!currentNode.parent) {
        return Promise.resolve([]);
      }

      const swfFunctionRefName: string = findNodeAtLocation(currentNode.parent, ["refName"])?.value;
      if (!swfFunctionRefName) {
        return Promise.resolve([]);
      }

      const swfFunction = swfModelQueries
        .getFunctions(rootNode)
        ?.filter((f) => f.name === swfFunctionRefName)
        .pop();
      if (!swfFunction) {
        return Promise.resolve([]);
      }

      const swfServiceCatalogFunc = swfCompletionItemServiceCatalogServices
        .flatMap((f) => f.functions)
        .filter((f) => f.operation === swfFunction.operation)
        .pop()!;
      if (!swfServiceCatalogFunc) {
        return Promise.resolve([]);
      }

      let argIndex = 1;
      const swfFunctionRefArgs: Record<string, string> = {};
      Object.keys(swfServiceCatalogFunc.arguments).forEach((argName) => {
        swfFunctionRefArgs[argName] = `$\{${argIndex++}:}`;
      });

      return Promise.resolve([
        {
          kind: CompletionItemKind.Module,
          label: `'${swfFunctionRefName}' arguments`,
          sortText: `${swfFunctionRefName} arguments`,
          detail: swfFunction.operation,
          textEdit: {
            newText: JSON.stringify(swfFunctionRefArgs, null, 2),
            range: overwriteRange,
          },
          insertTextFormat: InsertTextFormat.Snippet,
        },
      ]);
    },
  ],
]);

function toCompletionItemLabelPrefix(
  swfServiceCatalogFunction: SwfServiceCatalogFunction,
  specsDirRelativePosixPath: string
) {
  switch (swfServiceCatalogFunction.source.type) {
    case SwfServiceCatalogFunctionSourceType.LOCAL_FS:
      const fileName =
        swfServiceCatalogFunction.source.serviceFileAbsolutePath.split("/").pop() ??
        swfServiceCatalogFunction.source.serviceFileAbsolutePath;
      return toCompletionItemLabel(specsDirRelativePosixPath, fileName, swfServiceCatalogFunction.name);
    case SwfServiceCatalogFunctionSourceType.SERVICE_REGISTRY:
      return toCompletionItemLabel(
        swfServiceCatalogFunction.source.registry,
        swfServiceCatalogFunction.source.serviceId,
        swfServiceCatalogFunction.name
      );
    default:
      return "";
  }
}

// This is very similar to `jsonc.findNodeAtLocation`, but it allows the use of '*' as a wildcard selector.
// This means that unlike `jsonc.findNodeAtLocation`, this method always returns a list of nodes, which can be empty if no matches are found.
export function findNodesAtLocation(root: SwfLSNode | undefined, path: SwfJSONPath): SwfLSNode[] {
  if (!root) {
    return [];
  }

  let nodes: SwfLSNode[] = [root];

  for (const segment of path) {
    if (segment === "*") {
      nodes = nodes.flatMap((s) => s.children ?? []);
      continue;
    }

    if (typeof segment === "number") {
      const index = segment as number;
      nodes = nodes.flatMap((n) => {
        if (n.type !== "array" || index < 0 || !Array.isArray(n.children) || index >= n.children.length) {
          return [];
        }

        return [n.children[index]];
      });
    }

    if (typeof segment === "string") {
      nodes = nodes.flatMap((n) => {
        if (n.type !== "object" || !Array.isArray(n.children)) {
          return [];
        }

        for (const prop of n.children) {
          if (Array.isArray(prop.children) && prop.children[0].value === segment && prop.children.length === 2) {
            return [prop.children[1]];
          }
        }

        return [];
      });
    }
  }

  return nodes;
}

export function findNodeAtLocation(root: SwfLSNode, path: SwfJSONPath): SwfLSNode | undefined {
  return findNodesAtLocation(root, path)[0];
}

export function findNodeAtOffset(root: SwfLSNode, offset: number, includeRightBound?: boolean): SwfLSNode | undefined {
  return jsonc.findNodeAtOffset(root as jsonc.Node, offset, includeRightBound) as SwfLSNode;
}

function toCompletionItemLabel(namespace: string, resource: string, operation: string) {
  return `${namespace}»${resource}#${operation}`;
}
