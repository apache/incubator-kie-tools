import * as monaco from "monaco-editor";
import { languages, Position } from "monaco-editor";
import * as jsonc from "jsonc-parser";
import { JSONPath } from "vscode-json-languageservice";
import { SwfMonacoEditorCommandArgs, SwfMonacoEditorCommandIds } from "../commands";
import { SwfServiceCatalogSingleton } from "../../../serviceCatalog";
import { ChannelType } from "@kie-tools-core/editor/dist/api";
import { OperatingSystem } from "@kie-tools-core/operating-system";
import CodeLens = languages.CodeLens;

export function initJsonCodeLenses(
  commandIds: SwfMonacoEditorCommandIds,
  channelType: ChannelType,
  os: OperatingSystem | undefined
): void {
  monaco.languages.registerCodeLensProvider("json", {
    provideCodeLenses: (model, cancellationToken) => {
      if (cancellationToken.isCancellationRequested) {
        return;
      }

      const rootNode = jsonc.parseTree(model.getValue());
      if (!rootNode) {
        return;
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

          const newCursorPosition = model.getPositionAt(node.offset + 1);

          return [
            {
              id: commandIds["OpenFunctionsCompletionItems"],
              title: `+ Add function...`,
              arguments: [
                {
                  position,
                  node,
                  newCursorPosition,
                } as SwfMonacoEditorCommandArgs["OpenFunctionsCompletionItems"],
              ],
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
          const user = SwfServiceCatalogSingleton.get().getUser();
          if (user) {
            return [];
          }

          return [
            {
              id: commandIds["LogInToRhhcc"],
              title: `↪ Log in to Red Hat Hybrid Cloud Console...`,
              arguments: [{ position, node } as SwfMonacoEditorCommandArgs["LogInToRhhcc"]],
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

          const user = SwfServiceCatalogSingleton.get().getUser();
          if (!user) {
            return [];
          }

          const serviceRegistryUrl = SwfServiceCatalogSingleton.get().getServiceRegistryUrl();
          if (serviceRegistryUrl) {
            return [];
          }

          return [
            {
              id: commandIds["SetupServiceRegistryUrl"],
              title: `↪ Setup Service Registry URL...`,
              arguments: [{ position, node } as SwfMonacoEditorCommandArgs["SetupServiceRegistryUrl"]],
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

          const user = SwfServiceCatalogSingleton.get().getUser();
          if (!user) {
            return [];
          }

          const serviceRegistryUrl = SwfServiceCatalogSingleton.get().getServiceRegistryUrl();
          if (!serviceRegistryUrl) {
            return [];
          }

          return [
            {
              id: commandIds["RefreshServiceCatalogFromRhhcc"],
              title: `↺ Refresh Service Registry (${user.username})`,
              arguments: [{ position, node } as SwfMonacoEditorCommandArgs["RefreshServiceCatalogFromRhhcc"]],
            },
          ];
        },
      });

      const displayRhhccIntegration = channelType === ChannelType.VSCODE_DESKTOP && os === OperatingSystem.MACOS;

      const codeLenses: CodeLens[] = [
        ...(displayRhhccIntegration ? logInToRhhcc : []),
        ...(displayRhhccIntegration ? setupServiceRegistryUrl : []),
        ...(displayRhhccIntegration ? refreshServiceRegistry : []),
        ...addFunction,
      ];

      return {
        lenses: codeLenses.flatMap((s) => s),
        dispose: () => {},
      };
    },
  });

  return;
}

function createCodeLenses(args: {
  model: monaco.editor.ITextModel;
  rootNode: jsonc.Node;
  jsonPath: JSONPath;
  commandDelegates: (args: { position: Position; node: jsonc.Node }) => CodeLens["command"][];
  positionLensAt: "begin" | "end";
}) {
  const nodes = findNodesAtLocation(args.rootNode, args.jsonPath);
  return nodes.flatMap((node) => {
    // Only position at the end if the type is object or array and has at least one child.
    const position =
      args.positionLensAt === "end" &&
      (node.type === "object" || node.type === "array") &&
      (node.children?.length ?? 0) > 0
        ? args.model.getPositionAt(node.offset + node.length)
        : args.model.getPositionAt(node.offset);

    return args.commandDelegates({ position, node }).map((command) => ({
      command,
      range: {
        startLineNumber: position.lineNumber,
        endLineNumber: position.lineNumber,
        startColumn: position.column,
        endColumn: position.column,
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
