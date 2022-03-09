import * as monaco from "monaco-editor";
import { languages, Position } from "monaco-editor";
import * as jsonc from "jsonc-parser";
import { JSONPath } from "vscode-json-languageservice";
import { SwfMonacoEditorCommandIds } from "../commands";
import CodeLens = languages.CodeLens;

export function initJsonCodeLenses(commandIds: SwfMonacoEditorCommandIds): void {
  monaco.languages.registerCodeLensProvider("json", {
    provideCodeLenses: (model, cancellationToken) => {
      if (cancellationToken.isCancellationRequested) {
        return;
      }

      const rootNode = jsonc.parseTree(model.getValue());
      if (!rootNode) {
        return;
      }

      const codeLenses: CodeLens[] = [
        // TODO: Implement code lenses
        // Follow this example
        //
        // createCodeLenses({
        //   model,
        //   rootNode,
        //   jsonPath: ["functions"],
        //   positionLensAt: "begin",
        //   commandDelegate: ({ position }) => ({
        //     id: commandIds["OpenFunctionsWidget"],
        //     title: `◎ Discover`,
        //     arguments: [{ position }],
        //   }),
        // }),
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
  commandDelegate: (args: { position: Position; node: jsonc.Node }) => CodeLens["command"];
  positionLensAt: "begin" | "end";
}) {
  const nodes = findNodesAtLocation(args.rootNode, args.jsonPath);
  return nodes.map((node) => {
    let position;
    if (args.positionLensAt === "begin") {
      position = args.model.getPositionAt(node.offset);
    } else {
      position = args.model.getPositionAt(node.offset + node.length);
    }

    return {
      command: args.commandDelegate({ position, node }),
      range: {
        startLineNumber: position.lineNumber,
        endLineNumber: position.lineNumber,
        startColumn: position.column,
        endColumn: position.column,
      },
    };
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
