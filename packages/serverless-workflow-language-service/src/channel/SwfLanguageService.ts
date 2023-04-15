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
  doRefValidation,
  EditorLanguageService,
  EditorLanguageServiceArgs,
  ELsCompletionsMap,
  ELsJsonPath,
  ELsNode,
  findNodesAtLocation,
} from "@kie-tools/editor-language-service/dist/channel";
import {
  SwfCatalogSourceType,
  SwfServiceCatalogFunction,
  SwfServiceCatalogFunctionSource,
  SwfServiceCatalogService,
  SwfServiceCatalogServiceType,
} from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import * as jsonc from "jsonc-parser";
import { posix as posixPath } from "path";
import { TextDocument } from "vscode-languageserver-textdocument";
import { CodeLens, CompletionItem, Diagnostic, DiagnosticSeverity, Position, Range } from "vscode-languageserver-types";
import {
  SwfLanguageServiceCodeCompletion,
  SwfLanguageServiceCodeCompletionFunctionsArgs,
} from "./SwfLanguageServiceCodeCompletion";
import { SwfLanguageServiceCodeLenses } from "./SwfLanguageServiceCodeLenses";
import { swfRefValidationMap } from "./swfRefValidationMap";
import { CodeCompletionStrategy } from "./types";

export type SwfLanguageServiceConfig = {
  shouldConfigureServiceRegistries: () => boolean; //TODO: See https://issues.redhat.com/browse/KOGITO-7107
  shouldServiceRegistriesLogIn: () => boolean; //TODO: See https://issues.redhat.com/browse/KOGITO-7107
  canRefreshServices: () => boolean; //TODO: See https://issues.redhat.com/browse/KOGITO-7107
  getSpecsDirPosixPaths: (
    textDocument: TextDocument
  ) => Promise<{ specsDirRelativePosixPath: string; specsDirAbsolutePosixPath: string }>;
  getRoutesDirPosixPaths: (
    textDocument: TextDocument
  ) => Promise<{ routesDirRelativePosixPath: string; routesDirAbsolutePosixPath: string }>;
  shouldDisplayServiceRegistriesIntegration: () => Promise<boolean>;
  shouldReferenceServiceRegistryFunctionsWithUrls: () => Promise<boolean>;
  shouldIncludeJsonSchemaDiagnostics: () => Promise<boolean>;
};

export type SwfLanguageServiceArgs = EditorLanguageServiceArgs & {
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
  jqCompletions: {
    remote: {
      getJqAutocompleteProperties: (args: {
        textDocument: TextDocument;
        schemaPaths: string[];
      }) => Promise<Record<string, string>[]>;
    };
    relative: {
      getJqAutocompleteProperties: (args: {
        textDocument: TextDocument;
        schemaPaths: string[];
      }) => Promise<Record<string, string>[]>;
    };
  };
  config: SwfLanguageServiceConfig;
};

export function isVirtualRegistry(serviceCatalogFunction: SwfServiceCatalogFunction): boolean {
  return (
    serviceCatalogFunction.source.type === "SERVICE_REGISTRY" && serviceCatalogFunction.source.registry === "Virtual"
  );
}

export class SwfLanguageService {
  private readonly els: EditorLanguageService;

  constructor(private readonly args: SwfLanguageServiceArgs) {
    this.els = new EditorLanguageService(this.args);
  }

  public async getCompletionItems(args: {
    content: string;
    uri: string;
    cursorPosition: Position;
    cursorWordRange: Range;
    rootNode: ELsNode | undefined;
    codeCompletionStrategy: CodeCompletionStrategy;
  }): Promise<CompletionItem[]> {
    const doc = TextDocument.create(args.uri, this.args.lang.fileLanguage, 0, args.content);

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

    return this.els.getCompletionItems({
      ...args,
      completions,
      extraCompletionFunctionsArgs: {
        langServiceConfig: this.args.config,
        swfCompletionItemServiceCatalogServices,
        jqCompletions: this.args.jqCompletions,
      },
    });
  }

  public async getCodeLenses(args: {
    content: string;
    uri: string;
    rootNode: ELsNode | undefined;
    codeCompletionStrategy: CodeCompletionStrategy;
  }): Promise<CodeLens[]> {
    const displayRhhccIntegration = await this.args.config.shouldDisplayServiceRegistriesIntegration();

    return this.els.getCodeLenses({
      ...args,
      codeLenses: SwfLanguageServiceCodeLenses,
      extraCodeLensesFunctionsArgs: {
        config: this.args.config,
        displayRhhccIntegration,
      },
    });
  }

  private getFunctionDiagnostics(services: SwfServiceCatalogService[]): Diagnostic[] {
    return services.flatMap((value) => this.generateDiagnostic(value.functions));
  }

  private generateDiagnostic(serviceCatalogFunctions: SwfServiceCatalogFunction[]): Diagnostic[] {
    const functionsWithoutName = serviceCatalogFunctions.filter((fs) => !fs.name && !isVirtualRegistry(fs));

    return functionsWithoutName.length >= 1
      ? [
          Diagnostic.create(
            Range.create(Position.create(0, 0), Position.create(0, 0)),
            this.getWarningMessage(serviceCatalogFunctions[0].source),
            DiagnosticSeverity.Warning
          ),
        ]
      : [];
  }

  private getWarningMessage(swfServiceCatalogFunctionSource: SwfServiceCatalogFunctionSource): string {
    if (swfServiceCatalogFunctionSource.type == "SERVICE_REGISTRY") {
      return `The ${swfServiceCatalogFunctionSource.serviceId} service in the  ${swfServiceCatalogFunctionSource.registry} registry is missing the "operationId" property in at least one operation`;
    }
    if (swfServiceCatalogFunctionSource.type === "LOCAL_FS") {
      return `The ${swfServiceCatalogFunctionSource.serviceFileAbsolutePath} service is missing the "operationId" property in at least one operation`;
    }
    return "";
  }

  public async getDiagnostics(args: {
    content: string;
    uriPath: string;
    rootNode: ELsNode | undefined;
    getSchemaDiagnostics: (textDocument: TextDocument, fileMatch: string[]) => Promise<Diagnostic[]>;
  }): Promise<Diagnostic[]> {
    if (!args.rootNode) {
      return [];
    }

    // this ensure the document is validated again
    const docVersion = Math.floor(Math.random() * 1000);

    const textDocument = TextDocument.create(
      args.uriPath,
      `serverless-workflow-${this.args.lang.fileLanguage}`,
      docVersion,
      args.content
    );
    const refValidationResults = doRefValidation({
      textDocument,
      rootNode: args.rootNode,
      validationMap: swfRefValidationMap,
    });
    const schemaValidationResults = (await this.args.config.shouldIncludeJsonSchemaDiagnostics())
      ? await args.getSchemaDiagnostics(textDocument, this.args.lang.fileMatch)
      : [];

    const doc = TextDocument.create(args.uriPath, this.args.lang.fileLanguage, 0, args.content);
    const globalServices = await this.args.serviceCatalog.global.getServices();
    const relativeServices = await this.args.serviceCatalog.relative.getServices(doc);
    return [
      ...schemaValidationResults,
      ...refValidationResults,
      ...this.getFunctionDiagnostics([...globalServices, ...relativeServices]),
    ];
  }

  public dispose() {
    this.els.dispose();
  }

  private async getSwfCompletionItemServiceCatalogFunctionOperation(
    containingService: SwfServiceCatalogService,
    func: SwfServiceCatalogFunction,
    document: TextDocument
  ): Promise<string> {
    const { specsDirRelativePosixPath } = await this.args.config.getSpecsDirPosixPaths(document);
    const { routesDirRelativePosixPath } = await this.args.config.getRoutesDirPosixPaths(document);

    let dirRelativePosixPath;

    if (containingService.type === SwfServiceCatalogServiceType.camelroute) {
      dirRelativePosixPath = routesDirRelativePosixPath;
    } else {
      dirRelativePosixPath = specsDirRelativePosixPath;
    }

    if (func.source.type === SwfCatalogSourceType.LOCAL_FS) {
      const serviceFileName = posixPath.basename(func.source.serviceFileAbsolutePath);
      const serviceFileRelativePosixPath = posixPath.join(dirRelativePosixPath, serviceFileName);
      return `${serviceFileRelativePosixPath}#${func.name}`;
    } else if (
      (await this.args.config.shouldReferenceServiceRegistryFunctionsWithUrls()) &&
      containingService.source.type === SwfCatalogSourceType.SERVICE_REGISTRY &&
      func.source.type === SwfCatalogSourceType.SERVICE_REGISTRY
    ) {
      return `${containingService.source.url}#${func.name}`;
    } else if (
      containingService.source.type === SwfCatalogSourceType.SERVICE_REGISTRY &&
      func.source.type === SwfCatalogSourceType.SERVICE_REGISTRY
    ) {
      const serviceFileName = await this.args.serviceCatalog.getServiceFileNameFromSwfServiceCatalogServiceId(
        containingService.source.registry,
        containingService.source.id
      );
      const serviceFileRelativePosixPath = posixPath.join(dirRelativePosixPath, serviceFileName);
      return `${serviceFileRelativePosixPath}#${func.name}`;
    } else {
      throw new Error("Unknown Service Catalog function source type");
    }
  }
}

const completions: ELsCompletionsMap<SwfLanguageServiceCodeCompletionFunctionsArgs> = new Map([
  [null, SwfLanguageServiceCodeCompletion.getEmptyFileCodeCompletions],
  [["start"], SwfLanguageServiceCodeCompletion.getStartCompletions],
  [["functions", "*"], SwfLanguageServiceCodeCompletion.getFunctionCompletions],
  [["functions", "*", "operation"], SwfLanguageServiceCodeCompletion.getFunctionOperationCompletions],
  [["events", "*"], SwfLanguageServiceCodeCompletion.getEventsCompletions],
  [["states", "*"], SwfLanguageServiceCodeCompletion.getStatesCompletions],
  [["states", "*", "actions", "*", "functionRef"], SwfLanguageServiceCodeCompletion.getFunctionRefCompletions],
  [
    ["states", "*", "actions", "*", "functionRef", "refName"],
    SwfLanguageServiceCodeCompletion.getFunctionRefRefnameCompletions,
  ],
  [
    ["states", "*", "actions", "*", "functionRef", "arguments"],
    SwfLanguageServiceCodeCompletion.getFunctionRefArgumentsCompletions,
  ],
  [["states", "*", "actions", "*", "functionRef", "arguments", "*"], SwfLanguageServiceCodeCompletion.getJqcompletions],
  [["states", "*", "actions", "*", "actionDataFilter", "*"], SwfLanguageServiceCodeCompletion.getJqcompletions],
  [["states", "*", "stateDataFilter", "*"], SwfLanguageServiceCodeCompletion.getJqcompletions],
  [["states", "*", "onEvents", "*", "eventDataFilter", "*"], SwfLanguageServiceCodeCompletion.getJqcompletions],
  [["states", "*", "eventDataFilter", "*"], SwfLanguageServiceCodeCompletion.getJqcompletions],
  [["states", "*", "dataConditions", "*", "condition"], SwfLanguageServiceCodeCompletion.getJqcompletions],
  [["states", "*", "onEvents", "*", "eventRefs", "*"], SwfLanguageServiceCodeCompletion.getEventRefsCompletions],
  [["states", "*", "transition"], SwfLanguageServiceCodeCompletion.getTransitionCompletions],
  [["states", "*", "dataConditions", "*", "transition"], SwfLanguageServiceCodeCompletion.getTransitionCompletions],
  [["states", "*", "defaultCondition", "transition"], SwfLanguageServiceCodeCompletion.getTransitionCompletions],
  [["states", "*", "eventConditions", "*", "transition"], SwfLanguageServiceCodeCompletion.getTransitionCompletions],
]);

export function findNodeAtLocation(root: ELsNode, path: ELsJsonPath): ELsNode | undefined {
  return findNodesAtLocation({ root, path })[0];
}

export function findNodeAtOffset(root: ELsNode, offset: number, includeRightBound?: boolean): ELsNode | undefined {
  return jsonc.findNodeAtOffset(root as jsonc.Node, offset, includeRightBound) as ELsNode;
}

export function getNodePath(node: ELsNode): ELsJsonPath {
  return jsonc.getNodePath(node as jsonc.Node);
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
