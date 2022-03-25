import {
  CodeLens,
  CompletionItem,
  CompletionItemKind,
  InsertTextFormat,
  Position,
  Range,
} from "vscode-languageserver-types";
import { TextDocument } from "vscode-languageserver-textdocument";
import * as jsonc from "jsonc-parser";
import { JSONPath } from "jsonc-parser";
import {
  SwfLanguageServiceChannelApi,
  SwfLanguageServiceCommandArgs,
  SwfLanguageServiceCommandExecution,
} from "@kie-tools/serverless-workflow-language-service";
import { Specification } from "@severlessworkflow/sdk-typescript";
import {
  SwfServiceCatalogFunction,
  SwfServiceCatalogFunctionSourceType,
  SwfServiceCatalogService,
} from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import * as swfModelQueries from "./modelQueries";
import * as vscode from "vscode";
import { RhhccAuthenticationStore } from "../rhhcc/RhhccAuthenticationStore";
import { SwfVsCodeExtensionConfiguration } from "../configuration";

const completions = new Map<
  jsonc.JSONPath,
  (args: {
    model: TextDocument;
    cursorPosition: Position;
    currentNode: jsonc.Node;
    overwriteRange: Range;
    currentNodePosition: { start: Position; end: Position };
    rootNode: jsonc.Node;
  }) => CompletionItem[]
>([
  [
    ["functions", "*"],
    ({ currentNode, rootNode, overwriteRange }) => {
      const separator = currentNode.type === "object" ? "," : "";
      const existingOperations = [] as string[]; // FIXME: tiago swfModelQueries.getFunctions(rootNode).map((f) => f.operation);

      return ([] as SwfServiceCatalogService[]) //FIXME: tiago
        .flatMap((swfServiceCatalogService) =>
          swfServiceCatalogService.functions
            .filter((swfServiceCatalogFunc) => !existingOperations.includes(swfServiceCatalogFunc.operation))
            .map((swfServiceCatalogFunc) => {
              const swfFunction: Omit<Specification.Function, "normalize"> = {
                name: `$\{1:${swfServiceCatalogFunc.name}}`,
                operation: swfServiceCatalogFunc.operation,
                type: swfServiceCatalogFunc.type,
              };

              const command: SwfLanguageServiceCommandExecution<"ImportFunctionFromCompletionItem"> = {
                name: "ImportFunctionFromCompletionItem",
                args: {
                  containingService: swfServiceCatalogService,
                },
              };

              return {
                kind:
                  swfServiceCatalogFunc.source.type === SwfServiceCatalogFunctionSourceType.RHHCC_SERVICE_REGISTRY
                    ? CompletionItemKind.Interface
                    : CompletionItemKind.Reference,
                label: toCompletionItemLabelPrefix(swfServiceCatalogFunc) + swfServiceCatalogFunc.name,
                detail:
                  swfServiceCatalogFunc.source.type === SwfServiceCatalogFunctionSourceType.RHHCC_SERVICE_REGISTRY
                    ? ""
                    : swfServiceCatalogFunc.operation,
                textEdit: {
                  newText: JSON.stringify(swfFunction, null, 2) + separator,
                  range: overwriteRange,
                },
                insertTextFormat: InsertTextFormat.Snippet,
                command: {
                  command: command.name,
                  title: "Import function from completion item",
                  arguments: [command.args],
                },
              };
            })
        );
    },
  ],
  [
    ["functions", "*", "operation"],
    ({ currentNode, rootNode, overwriteRange }) => {
      if (!currentNode.parent?.parent) {
        return [];
      }

      // As "rest" is the default, if the value is undefined, it's a rest function too.
      const isRestFunction =
        (jsonc.findNodeAtLocation(currentNode.parent.parent, ["type"])?.value ?? "rest") === "rest";
      if (!isRestFunction) {
        return [];
      }

      const existingOperations = swfModelQueries.getFunctions(rootNode).map((f) => f.operation);
      return ([] as SwfServiceCatalogFunction[]) //FIXME: tiago
        .filter((swfServiceCatalogFunc) => !existingOperations.includes(swfServiceCatalogFunc.operation))
        .map((swfServiceCatalogFunc) => {
          return {
            kind: CompletionItemKind.Value,
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
    },
  ],
  [
    ["states", "*", "actions", "*", "functionRef"],
    ({ overwriteRange, currentNode, rootNode }) => {
      if (currentNode.type !== "property") {
        console.debug("Cannot autocomplete: functionRef should be a property.");
        return [];
      }

      return swfModelQueries.getFunctions(rootNode).flatMap((swfFunction) => {
        const swfServiceCatalogFunc = {} as SwfServiceCatalogFunction; //FIXME: tiago //SwfServiceCatalogSingleton.get().getFunctionByOperation(swfFunction.operation);
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
    },
  ],
  [
    ["states", "*", "actions", "*", "functionRef", "refName"],
    ({ overwriteRange, rootNode }) => {
      return swfModelQueries.getFunctions(rootNode).flatMap((swfFunction) => {
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
    },
  ],
  [
    ["states", "*", "actions", "*", "functionRef", "arguments"],
    ({ overwriteRange, currentNode, rootNode }) => {
      if (currentNode.type !== "property") {
        console.debug("Cannot autocomplete: functionRef should be a property.");
        return [];
      }

      if (!currentNode.parent) {
        return [];
      }

      const swfFunctionRefName: string = jsonc.findNodeAtLocation(currentNode.parent, ["refName"])?.value;
      if (!swfFunctionRefName) {
        return [];
      }

      const swfFunction = swfModelQueries
        .getFunctions(rootNode)
        ?.filter((f) => f.name === swfFunctionRefName)
        .pop();
      if (!swfFunction) {
        return [];
      }

      const swfServiceCatalogFunc = {} as SwfServiceCatalogFunction; //FIXME: tiago //SwfServiceCatalogSingleton.get().getFunctionByOperation(swfFunction.operation);
      if (!swfServiceCatalogFunc) {
        return [];
      }

      let argIndex = 1;
      const swfFunctionRefArgs: Record<string, string> = {};
      Object.keys(swfServiceCatalogFunc.arguments).forEach((argName) => {
        swfFunctionRefArgs[argName] = `$\{${argIndex++}:}`;
      });

      return [
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
      ];
    },
  ],
]);

function toCompletionItemLabelPrefix(swfServiceCatalogFunction: SwfServiceCatalogFunction) {
  switch (swfServiceCatalogFunction.source.type) {
    case SwfServiceCatalogFunctionSourceType.LOCAL_FS:
      return "fs: ";
    case SwfServiceCatalogFunctionSourceType.RHHCC_SERVICE_REGISTRY:
      return `${swfServiceCatalogFunction.source.serviceId}: `;
    default:
      return "";
  }
}

function createCodeLenses(args: {
  model: TextDocument;
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
        ? args.model.positionAt(node.offset + node.length)
        : args.model.positionAt(node.offset);

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

export class SwfLanguageServiceChannelApiImpl implements SwfLanguageServiceChannelApi {
  constructor(
    private readonly args: {
      rhhccAuthenticationStore: RhhccAuthenticationStore;
      configuration: SwfVsCodeExtensionConfiguration;
    }
  ) {}
  public async kogitoSwfLanguageService__getCompletionItems(args: {
    content: string;
    uri: string;
    cursorPosition: Position;
    cursorWordRange: Range;
  }): Promise<CompletionItem[]> {
    const model = TextDocument.create(args.uri, "serverless-workflow-json", 0, args.content);
    const rootNode = jsonc.parseTree(args.content);
    if (!rootNode) {
      return [];
    }

    const cursorOffset = model.offsetAt(args.cursorPosition);

    const currentNode = jsonc.findNodeAtOffset(rootNode, cursorOffset);
    if (!currentNode) {
      return [];
    }

    const currentNodePosition = {
      start: model.positionAt(currentNode.offset),
      end: model.positionAt(currentNode.offset + currentNode.length),
    };

    const overwriteRange = ["string", "number", "boolean", "null"].includes(currentNode?.type)
      ? Range.create(currentNodePosition.start, currentNodePosition.end)
      : args.cursorWordRange;

    const cursorJsonLocation = jsonc.getLocation(args.content, cursorOffset);

    return Array.from(completions.entries())
      .filter(([path, _]) => cursorJsonLocation.matches(path) && cursorJsonLocation.path.length === path.length)
      .flatMap(([_, completionItemsDelegate]) =>
        completionItemsDelegate({
          model,
          cursorPosition: args.cursorPosition,
          currentNode,
          currentNodePosition,
          rootNode,
          overwriteRange,
        })
      );
  }

  public async kogitoSwfLanguageService__getCodeLenses(args: { uri: string; content: string }): Promise<CodeLens[]> {
    const model = TextDocument.create(args.uri, "serverless-workflow-json", 0, args.content);

    const rootNode = jsonc.parseTree(model.getText());
    if (!rootNode) {
      return [];
    }

    const addFunction = createCodeLenses({
      model,
      rootNode,
      jsonPath: ["functions"],
      positionLensAt: "begin",
      commandDelegates: ({ position, node }) => {
        if (node.type !== "array") {
          return [];
        }

        const newCursorPosition = model.positionAt(node.offset + 1);

        return [
          {
            name: "OpenFunctionsCompletionItems",
            title: `+ Add function...`,
            args: [{ newCursorPosition } as SwfLanguageServiceCommandArgs["OpenFunctionsCompletionItems"]],
          },
        ];
      },
    });

    const logInToRhhcc = createCodeLenses({
      model,
      rootNode,
      jsonPath: ["functions"],
      positionLensAt: "begin",
      commandDelegates: ({ position, node }) => {
        if (node.type !== "array") {
          return [];
        }

        const userName = this.args.rhhccAuthenticationStore.session?.account.label;
        if (userName) {
          return [];
        }

        return [
          {
            name: "LogInToRhhcc",
            title: `↪ Log in to Red Hat Hybrid Cloud Console...`,
            args: [{ position } as SwfLanguageServiceCommandArgs["LogInToRhhcc"]],
          },
        ];
      },
    });

    const setupServiceRegistryUrl = createCodeLenses({
      model,
      rootNode,
      jsonPath: ["functions"],
      positionLensAt: "begin",
      commandDelegates: ({ position, node }) => {
        if (node.type !== "array") {
          return [];
        }

        const userName = this.args.rhhccAuthenticationStore.session?.account.label;
        if (!userName) {
          return [];
        }

        const serviceRegistryUrl = this.args.configuration.getConfiguredServiceRegistryUrl();
        if (serviceRegistryUrl) {
          return [];
        }

        return [
          {
            name: "SetupServiceRegistryUrl",
            title: `↪ Setup Service Registry URL...`,
            args: [{ position } as SwfLanguageServiceCommandArgs["SetupServiceRegistryUrl"]],
          },
        ];
      },
    });

    const refreshServiceRegistry = createCodeLenses({
      model,
      rootNode,
      jsonPath: ["functions"],
      positionLensAt: "begin",
      commandDelegates: ({ position, node }) => {
        if (node.type !== "array") {
          return [];
        }

        const userName = this.args.rhhccAuthenticationStore.session?.account.label;
        if (!userName) {
          return [];
        }

        const serviceRegistryUrl = this.args.configuration.getConfiguredServiceRegistryUrl();
        if (!serviceRegistryUrl) {
          return [];
        }

        return [
          {
            name: "RefreshServiceCatalogFromRhhcc",
            title: `↺ Refresh Service Registry (${userName})`,
            args: [{ position } as SwfLanguageServiceCommandArgs["RefreshServiceCatalogFromRhhcc"]],
          },
        ];
      },
    });

    const displayRhhccIntegration = vscode.env.uiKind === vscode.UIKind.Desktop;

    return [
      ...(displayRhhccIntegration ? logInToRhhcc : []),
      ...(displayRhhccIntegration ? setupServiceRegistryUrl : []),
      ...(displayRhhccIntegration ? refreshServiceRegistry : []),
      ...addFunction,
    ];
  }
}
