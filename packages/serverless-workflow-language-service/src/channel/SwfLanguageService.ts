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
  SwfServiceCatalogFunction,
  SwfServiceCatalogFunctionSourceType,
  SwfServiceCatalogService,
  SwfServiceCatalogServiceSourceType,
} from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import { Specification } from "@severlessworkflow/sdk-typescript";
import * as jsonc from "jsonc-parser";
import { posix as posixPath } from "path";
import { getLanguageService, TextDocument } from "vscode-json-languageservice";
import {
  CodeLens,
  CompletionItem,
  CompletionItemKind,
  InsertTextFormat,
  Position,
  Range,
} from "vscode-languageserver-types";
import { FileLanguage, SwfLanguageServiceCommandArgs, SwfLanguageServiceCommandExecution } from "../api";
import { SW_SPEC_WORKFLOW_SCHEMA } from "../schemas";
import { findNodesAtLocation } from "./findNodesAtLocation";
import * as swfModelQueries from "./modelQueries";
import { nodeUpUntilType } from "./nodeUpUntilType";
import { doRefValidation } from "./refValidation";
import { CodeCompletionStrategy, SwfJsonPath, SwfLsNode } from "./types";

export type SwfLanguageServiceConfig = {
  shouldConfigureServiceRegistries: () => boolean; //TODO: See https://issues.redhat.com/browse/KOGITO-7107
  shouldServiceRegistriesLogIn: () => boolean; //TODO: See https://issues.redhat.com/browse/KOGITO-7107
  canRefreshServices: () => boolean; //TODO: See https://issues.redhat.com/browse/KOGITO-7107
  getSpecsDirPosixPaths: (
    textDocument: TextDocument
  ) => Promise<{ specsDirRelativePosixPath: string; specsDirAbsolutePosixPath: string }>;
  shouldDisplayServiceRegistriesIntegration: () => Promise<boolean>;
  shouldReferenceServiceRegistryFunctionsWithUrls: () => Promise<boolean>;
  shouldIncludeJsonSchemaDiagnostics: () => Promise<boolean>;
};

export type SwfLanguageServiceArgs = {
  fs: {};
  lang: {
    fileLanguage: FileLanguage;
    fileMatch: string[];
  };
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

export class SwfLanguageService {
  constructor(private readonly args: SwfLanguageServiceArgs) {}

  public async getCompletionItems(args: {
    content: string;
    uri: string;
    cursorPosition: Position;
    cursorWordRange: Range;
    rootNode: SwfLsNode | undefined;
    codeCompletionStrategy: CodeCompletionStrategy;
  }): Promise<CompletionItem[]> {
    if (!args.rootNode) {
      return [];
    }

    const doc = TextDocument.create(args.uri, this.args.lang.fileLanguage, 0, args.content);
    const cursorOffset = doc.offsetAt(args.cursorPosition);

    const currentNode = findNodeAtOffset(args.rootNode, cursorOffset, true);
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

    const matchedCompletions = Array.from(completions.entries()).filter(([path, _]) =>
      args.codeCompletionStrategy.shouldComplete({
        root: args.rootNode,
        node: currentNode,
        path: path,
        content: args.content,
        cursorOffset: cursorOffset,
      })
    );

    const result = await Promise.all(
      matchedCompletions.map(([_, completionItemsDelegate]) => {
        return completionItemsDelegate({
          document: doc,
          cursorPosition: args.cursorPosition,
          currentNode,
          currentNodePosition,
          rootNode: args.rootNode!,
          overwriteRange,
          swfCompletionItemServiceCatalogServices,
          langServiceConfig: this.args.config,
          codeCompletionStrategy: args.codeCompletionStrategy,
        });
      })
    );

    return Promise.resolve(result.flat());
  }

  public async getDiagnostics(args: { content: string; uriPath: string; rootNode: SwfLsNode | undefined }) {
    if (!args.rootNode) {
      return [];
    }

    const textDocument = TextDocument.create(
      args.uriPath,
      `serverless-workflow-${this.args.lang.fileLanguage}`,
      1,
      args.content
    );

    const refValidationResults = doRefValidation({ textDocument, rootNode: args.rootNode });

    if (this.args.lang.fileLanguage === FileLanguage.YAML) {
      //TODO: Include JSON Schema validation for YAML as well. Probably use what the YAML extension uses?
      return refValidationResults;
    }

    const schemaValidationResults = (await this.args.config.shouldIncludeJsonSchemaDiagnostics())
      ? await this.getJsonSchemaDiagnostics(textDocument)
      : [];

    return [...schemaValidationResults, ...refValidationResults];
  }

  private async getJsonSchemaDiagnostics(textDocument: TextDocument) {
    const jsonLs = getLanguageService({
      schemaRequestService: async (uri) => {
        if (uri === SW_SPEC_WORKFLOW_SCHEMA.$id) {
          return JSON.stringify(SW_SPEC_WORKFLOW_SCHEMA);
        } else {
          throw new Error(`Unable to load schema from '${uri}'`);
        }
      },
    });

    jsonLs.configure({
      allowComments: false,
      schemas: [{ fileMatch: this.args.lang.fileMatch, uri: SW_SPEC_WORKFLOW_SCHEMA.$id }],
    });

    const jsonDocument = jsonLs.parseJSONDocument(textDocument);
    return jsonLs.doValidation(textDocument, jsonDocument);
  }

  public async getCodeLenses(args: {
    content: string;
    uri: string;
    rootNode: SwfLsNode | undefined;
    codeCompletionStrategy: CodeCompletionStrategy;
  }): Promise<CodeLens[]> {
    if (!args.rootNode) {
      return [];
    }

    const document = TextDocument.create(args.uri, this.args.lang.fileLanguage, 0, args.content);

    const addFunction = this.createCodeLenses({
      document,
      rootNode: args.rootNode,
      jsonPath: ["functions"],
      positionLensAt: "begin",
      commandDelegates: ({ node }) => {
        if (node.type !== "array") {
          return [];
        }

        const newCursorPosition = args.codeCompletionStrategy.getStartNodeValuePosition(document, node);

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
      rootNode: args.rootNode,
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
      rootNode: args.rootNode,
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
      rootNode: args.rootNode,
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

  public createCodeLenses(args: {
    document: TextDocument;
    rootNode: SwfLsNode;
    jsonPath: SwfJsonPath;
    commandDelegates: (args: {
      position: Position;
      node: SwfLsNode;
    }) => ({ title: string } & SwfLanguageServiceCommandExecution<any>)[];
    positionLensAt: "begin" | "end";
  }): CodeLens[] {
    const nodes = findNodesAtLocation({ root: args.rootNode, path: args.jsonPath });
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
  SwfJsonPath,
  (args: {
    swfCompletionItemServiceCatalogServices: SwfCompletionItemServiceCatalogService[];
    document: TextDocument;
    cursorPosition: Position;
    currentNode: SwfLsNode;
    overwriteRange: Range;
    currentNodePosition: { start: Position; end: Position };
    rootNode: SwfLsNode;
    langServiceConfig: SwfLanguageServiceConfig;
    codeCompletionStrategy: CodeCompletionStrategy;
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
      codeCompletionStrategy,
    }) => {
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

            const kind =
              swfServiceCatalogFunc.source.type === SwfServiceCatalogFunctionSourceType.SERVICE_REGISTRY
                ? CompletionItemKind.Interface
                : CompletionItemKind.Reference;

            const label = codeCompletionStrategy.formatLabel(
              toCompletionItemLabelPrefix(swfServiceCatalogFunc, specsDir.specsDirRelativePosixPath),
              kind
            );

            return {
              kind,
              label,
              detail:
                swfServiceCatalogService.source.type === SwfServiceCatalogServiceSourceType.SERVICE_REGISTRY
                  ? swfServiceCatalogService.source.url
                  : swfServiceCatalogFunc.operation,
              textEdit: {
                newText:
                  codeCompletionStrategy.translate({
                    completion: swfFunction,
                    completionItemKind: kind,
                    overwriteRange,
                  }) + (currentNode.type === "object" ? "," : ""),
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
    ({ currentNode, rootNode, overwriteRange, swfCompletionItemServiceCatalogServices, codeCompletionStrategy }) => {
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
          const kind =
            swfServiceCatalogFunc.source.type === SwfServiceCatalogFunctionSourceType.SERVICE_REGISTRY
              ? CompletionItemKind.Function
              : CompletionItemKind.Folder;

          const label = codeCompletionStrategy.formatLabel(swfServiceCatalogFunc.operation, kind);

          return {
            kind,
            label,
            detail: label,
            filterText: label,
            textEdit: {
              newText: codeCompletionStrategy.translate({
                completion: `${swfServiceCatalogFunc.operation}`,
                completionItemKind: kind,
              }),
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
    ({ overwriteRange, currentNode, rootNode, swfCompletionItemServiceCatalogServices, codeCompletionStrategy }) => {
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

        const kind = CompletionItemKind.Module;
        const label = codeCompletionStrategy.formatLabel(swfFunctionRef.refName, kind);

        return [
          {
            kind,
            label,
            sortText: label,
            detail: `${swfServiceCatalogFunc.operation}`,
            textEdit: {
              newText: codeCompletionStrategy.translate({ completion: swfFunctionRef, completionItemKind: kind }),
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
    ({ overwriteRange, rootNode, codeCompletionStrategy }) => {
      const result = swfModelQueries.getFunctions(rootNode).flatMap((swfFunction) => {
        const kind = CompletionItemKind.Value;
        const label = codeCompletionStrategy.formatLabel(swfFunction.name, kind);

        return [
          {
            kind,
            label,
            sortText: label,
            detail: `"${swfFunction.name}"`,
            filterText: label,
            textEdit: {
              newText: codeCompletionStrategy.translate({
                completion: `${swfFunction.name}`,
                completionItemKind: kind,
              }),
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
    ({ overwriteRange, currentNode, rootNode, swfCompletionItemServiceCatalogServices, codeCompletionStrategy }) => {
      if (currentNode.type !== "property" && currentNode.type !== "string") {
        console.debug("Cannot autocomplete: arguments should be a property.");
        return Promise.resolve([]);
      }

      const startNode = nodeUpUntilType(currentNode, "object");

      if (!startNode) {
        return Promise.resolve([]);
      }

      const swfFunctionRefName: string = findNodeAtLocation(startNode, ["refName"])?.value;
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

      const kind = CompletionItemKind.Module;
      const label = `'${swfFunctionRefName}' arguments`;

      return Promise.resolve([
        {
          kind,
          label,
          sortText: label,
          detail: swfFunction.operation,
          textEdit: {
            newText: codeCompletionStrategy.translate({ completion: swfFunctionRefArgs, completionItemKind: kind }),
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

export function findNodeAtLocation(root: SwfLsNode, path: SwfJsonPath): SwfLsNode | undefined {
  return findNodesAtLocation({ root, path })[0];
}

export function findNodeAtOffset(root: SwfLsNode, offset: number, includeRightBound?: boolean): SwfLsNode | undefined {
  return jsonc.findNodeAtOffset(root as jsonc.Node, offset, includeRightBound) as SwfLsNode;
}

function toCompletionItemLabel(namespace: string, resource: string, operation: string) {
  return `${namespace}»${resource}#${operation}`;
}
