import {
  CodeLens,
  CompletionItem,
  CompletionItemKind,
  InsertTextFormat,
  Position,
  Range,
  TextDocumentIdentifier,
} from "vscode-languageserver-types";
import * as asd from "vscode-languageserver-textdocument";
import * as jsonc from "jsonc-parser";
import { SwfLanguageServiceChannelApi } from "@kie-tools/serverless-workflow-language-service";
import { Specification } from "@severlessworkflow/sdk-typescript";
import {
  SwfServiceCatalogFunction,
  SwfServiceCatalogFunctionSourceType,
  SwfServiceCatalogService,
} from "@kie-tools/serverless-workflow-service-catalog/src/api";
import * as swfModelQueries from "./modelQueries";
import { SwfMonacoEditorCommandIds } from "@kie-tools/serverless-workflow-editor/dist/editor/ServerlessWorkflowEditorEnvelopeApi";

const completions = new Map<
  jsonc.JSONPath,
  (args: {
    model: asd.TextDocument;
    cursorPosition: Position;
    currentNode: jsonc.Node;
    overwriteRange: Range;
    currentNodePosition: { start: Position; end: Position };
    commandIds: SwfMonacoEditorCommandIds;
    rootNode: jsonc.Node;
  }) => CompletionItem[]
>([
  [
    ["functions", "*"],
    ({ currentNode, rootNode, overwriteRange, commandIds }) => {
      const separator = currentNode.type === "object" ? "," : "";
      const existingOperations = [] as string[]; //swfModelQueries.getFunctions(rootNode).map((f) => f.operation);

      return ([] as SwfServiceCatalogService[]) //FIXME: tiago
        .flatMap((service) => {
          return service.functions
            .filter((swfServiceCatalogFunc) => !existingOperations.includes(swfServiceCatalogFunc.operation))
            .map((swfServiceCatalogFunc) => {
              const swfFunction: Omit<Specification.Function, "normalize"> = {
                name: `$\{1:${swfServiceCatalogFunc.name}}`,
                operation: swfServiceCatalogFunc.operation,
                type: swfServiceCatalogFunc.type,
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
                // command: {
                //   id: commandIds["ImportFunctionFromCompletionItem"],
                //   title: "Import function from completion item",
                //   arguments: [
                //     {
                //       containingService: service,
                //     } as SwfMonacoEditorCommandArgs["ImportFunctionFromCompletionItem"],
                //   ],
                // },
              };
            });
        });
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
            label: swfServiceCatalogFunc.operation,
            detail: swfServiceCatalogFunc.operation,
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
            sortText: swfFunctionRef.refName,
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
            label: swfFunction.name,
            sortText: swfFunction.name,
            detail: swfFunction.name,
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

export class SwfLanguageServiceChannelApiImpl implements SwfLanguageServiceChannelApi {
  public async kogitoSwfLanguageService__doCompletion(
    content: string,
    uri: string,
    cursorPosition: Position
  ): Promise<CompletionItem[]> {
    const model = asd.TextDocument.create(uri, "json", 0, content);
    const rootNode = jsonc.parseTree(content);
    if (!rootNode) {
      return [];
    }

    const cursorOffset = model.offsetAt(cursorPosition);

    const currentNode = jsonc.findNodeAtOffset(rootNode, cursorOffset);
    if (!currentNode) {
      return [];
    }

    const currentNodePosition = {
      start: model.positionAt(currentNode.offset),
      end: model.positionAt(currentNode.offset + currentNode.length),
    };

    // const currentWordPosition = model.getWordAtPosition(cursorPosition);

    const overwriteRange = ["string", "number", "boolean", "null"].includes(currentNode?.type)
      ? Range.create(currentNodePosition.start, currentNodePosition.end)
      : Range.create(cursorPosition, cursorPosition);
    // : Range.create(
    //     {
    //       line: cursorPosition.line,
    //       character: currentWordPosition?.startColumn ?? cursorPosition.character,
    //     },
    //     {
    //       line: cursorPosition.line,
    //       character: currentWordPosition?.endColumn ?? cursorPosition.character,
    //     }
    //   );

    const cursorJsonLocation = jsonc.getLocation(content, cursorOffset);

    console.error(cursorJsonLocation);

    return Array.from(completions.entries())
      .filter(([path, _]) => cursorJsonLocation.matches(path) && cursorJsonLocation.path.length === path.length)
      .flatMap(([_, completionItemsDelegate]) =>
        completionItemsDelegate({
          model,
          cursorPosition,
          commandIds: {} as SwfMonacoEditorCommandIds,
          currentNode,
          currentNodePosition,
          rootNode,
          overwriteRange,
        })
      );

    // return [
    //   {
    //     kind: CompletionItemKind.Value,
    //     label: "myCompletionItem",
    //     sortText: "myCompletionItem",
    //     detail: "myCompletionItem",
    //     filterText: "myCompletionItem",
    //     insertTextFormat: InsertTextFormat.Snippet,
    //     textEdit: {
    //       newText: "myCompletionItem",
    //       range: {
    //         start: position,
    //         end: position,
    //       },
    //     },
    //   },
    // ];
  }
  public async kogitoSwfLanguageService__doCodeLenses(
    textDocumentIdentifier: TextDocumentIdentifier
  ): Promise<CodeLens[]> {
    return [];
  }
}
