import { TextDocument } from "vscode-languageserver-textdocument";
import {
  CodeLens,
  CompletionItem,
  CompletionItemKind,
  InsertTextFormat,
  Position,
  Range,
} from "vscode-languageserver-types";
import * as jsonc from "jsonc-parser";
import { JSONPath } from "jsonc-parser";
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

export type SwfJsonLanguageServiceConfig = {
  isServiceRegistryConfigured: () => boolean; //TODO: See https://issues.redhat.com/browse/KOGITO-7107
  shouldServiceRegistryLogIn: () => boolean; //TODO: See https://issues.redhat.com/browse/KOGITO-7107
  canRefreshServices: () => boolean; //TODO: See https://issues.redhat.com/browse/KOGITO-7107
  getSpecsDirPosixPaths: (
    textDocument: TextDocument
  ) => Promise<{ specsDirRelativePosixPath: string; specsDirAbsolutePosixPath: string }>;
  shouldDisplayServiceRegistryIntegration: () => Promise<boolean>;
  shouldReferenceServiceRegistryFunctionsWithUrls: () => Promise<boolean>;
};

export class SwfJsonLanguageService {
  constructor(
    private readonly args: {
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
      config: SwfJsonLanguageServiceConfig;
    }
  ) {}

  public async getCompletionItems(args: {
    content: string;
    uri: string;
    cursorPosition: Position;
    cursorWordRange: Range;
  }): Promise<CompletionItem[]> {
    const doc = TextDocument.create(args.uri, "json", 0, args.content);
    const rootNode = jsonc.parseTree(args.content);
    if (!rootNode) {
      return [];
    }

    const cursorOffset = doc.offsetAt(args.cursorPosition);

    const currentNode = jsonc.findNodeAtOffset(rootNode, cursorOffset);
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

    const cursorJsonLocation = jsonc.getLocation(args.content, cursorOffset);

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

    const result = await Promise.all(
      Array.from(completions.entries())
        .filter(([path, _]) => cursorJsonLocation.matches(path) && cursorJsonLocation.path.length === path.length)
        .map(([_, completionItemsDelegate]) =>
          completionItemsDelegate({
            document: doc,
            cursorPosition: args.cursorPosition,
            currentNode,
            currentNodePosition,
            rootNode,
            overwriteRange,
            swfCompletionItemServiceCatalogServices,
            langServiceConfig: this.args.config,
          })
        )
    );

    return Promise.resolve(result.flat());
  }

  public async getCodeLenses(args: { content: string; uri: string }): Promise<CodeLens[]> {
    const document = TextDocument.create(args.uri, "json", 0, args.content);

    const rootNode = jsonc.parseTree(document.getText());
    if (!rootNode) {
      return [];
    }

    const addFunction = createCodeLenses({
      document,
      rootNode,
      jsonPath: ["functions"],
      positionLensAt: "begin",
      commandDelegates: ({ position, node }) => {
        if (node.type !== "array") {
          return [];
        }

        const newCursorPosition = document.positionAt(node.offset + 1);

        return [
          {
            name: "swf.ls.commands.OpenFunctionsCompletionItems",
            title: `+ Add function...`,
            args: [
              { newCursorPosition } as SwfLanguageServiceCommandArgs["swf.ls.commands.OpenFunctionsCompletionItems"],
            ],
          },
        ];
      },
    });

    const setupServiceRegistry = createCodeLenses({
      document,
      rootNode,
      jsonPath: ["functions"],
      positionLensAt: "begin",
      commandDelegates: ({ position, node }) => {
        if (node.type !== "array") {
          return [];
        }

        if (this.args.config.isServiceRegistryConfigured()) {
          return [];
        }

        return [
          {
            name: "swf.ls.commands.OpenServiceRegistryConfig",
            title: `↪ Setup Service Registry...`,
            args: [{ position } as SwfLanguageServiceCommandArgs["swf.ls.commands.OpenServiceRegistryConfig"]],
          },
        ];
      },
    });

    const logInServiceRegistry = createCodeLenses({
      document,
      rootNode,
      jsonPath: ["functions"],
      positionLensAt: "begin",
      commandDelegates: ({ position, node }) => {
        if (node.type !== "array") {
          return [];
        }

        if (!this.args.config.isServiceRegistryConfigured()) {
          return [];
        }

        if (!this.args.config.shouldServiceRegistryLogIn()) {
          return [];
        }

        return [
          {
            name: "swf.ls.commands.LogInServiceRegistry",
            title: `↪ Log in Service Registry...`,
            args: [{ position } as SwfLanguageServiceCommandArgs["swf.ls.commands.LogInServiceRegistry"]],
          },
        ];
      },
    });

    const refreshServiceRegistry = createCodeLenses({
      document,
      rootNode,
      jsonPath: ["functions"],
      positionLensAt: "begin",
      commandDelegates: ({ position, node }) => {
        if (node.type !== "array") {
          return [];
        }

        if (!this.args.config.isServiceRegistryConfigured()) {
          return [];
        }

        if (!this.args.config.canRefreshServices()) {
          return [];
        }

        return [
          {
            name: "swf.ls.commands.RefreshServiceRegistry",
            title: `↺ Refresh Service Registry`,
            args: [{ position } as SwfLanguageServiceCommandArgs["swf.ls.commands.RefreshServiceRegistry"]],
          },
        ];
      },
    });

    const displayRhhccIntegration = await this.args.config.shouldDisplayServiceRegistryIntegration();

    return [
      ...(displayRhhccIntegration ? setupServiceRegistry : []),
      ...(displayRhhccIntegration ? logInServiceRegistry : []),
      ...(displayRhhccIntegration ? refreshServiceRegistry : []),
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
}

type SwfCompletionItemServiceCatalogFunction = SwfServiceCatalogFunction & { operation: string };
type SwfCompletionItemServiceCatalogService = Omit<SwfServiceCatalogService, "functions"> & {
  functions: SwfCompletionItemServiceCatalogFunction[];
};

const completions = new Map<
  jsonc.JSONPath,
  (args: {
    swfCompletionItemServiceCatalogServices: SwfCompletionItemServiceCatalogService[];
    document: TextDocument;
    cursorPosition: Position;
    currentNode: jsonc.Node;
    overwriteRange: Range;
    currentNodePosition: { start: Position; end: Position };
    rootNode: jsonc.Node;
    langServiceConfig: SwfJsonLanguageServiceConfig;
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
      const isRestFunction =
        (jsonc.findNodeAtLocation(currentNode.parent.parent, ["type"])?.value ?? "rest") === "rest";
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

      const swfFunctionRefName: string = jsonc.findNodeAtLocation(currentNode.parent, ["refName"])?.value;
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

function toCompletionItemLabel(namespace: string, resource: string, operation: string) {
  return `${namespace}»${resource}#${operation}`;
}

function createCodeLenses(args: {
  document: TextDocument;
  rootNode: jsonc.Node;
  jsonPath: JSONPath;
  commandDelegates: (args: {
    position: Position;
    node: jsonc.Node;
  }) => ({ title: string } & SwfLanguageServiceCommandExecution<any>)[];
  positionLensAt: "begin" | "end";
}): CodeLens[] {
  const nodes = findNodesAtLocation(args.rootNode, args.jsonPath);
  return nodes.flatMap((node) => {
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
}

// This is very similar to `jsonc.findNodeAtLocation`, but it allows the use of '*' as a wildcard selector.
// This means that unlike `jsonc.findNodeAtLocation`, this method always returns a list of nodes, which can be empty if no matches are found.
export function findNodesAtLocation(root: jsonc.Node | undefined, path: JSONPath): jsonc.Node[] {
  if (!root) {
    return [];
  }

  let nodes: jsonc.Node[] = [root];

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
