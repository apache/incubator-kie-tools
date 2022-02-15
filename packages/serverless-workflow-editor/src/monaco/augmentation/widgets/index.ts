import * as monaco from "monaco-editor";
import { languages, Position } from "monaco-editor";
import * as jsonc from "jsonc-parser";
import { JSONPath } from "vscode-json-languageservice";
import { SwfMonacoEditorCommands } from "../../SwfMonacoEditorApi";
import CodeLens = languages.CodeLens;

function toLens(
  model: monaco.editor.ITextModel,
  rootNode: jsonc.Node,
  jsonPath: JSONPath,
  commandDelegate: (args: { position: Position; node: jsonc.Node }) => CodeLens["command"],
  positionLensAt: "begin" | "end" = "begin"
) {
  const nodes = findNodesAtLocation(rootNode, jsonPath);
  return nodes.map((node) => {
    let position;
    if (positionLensAt === "begin") {
      position = model.getPositionAt(node.offset);
    } else {
      const pos = model.getPositionAt(node.offset + node.length);
      position = new Position(pos.lineNumber, pos.column);
    }

    return {
      command: commandDelegate({ position, node }),
      range: {
        startLineNumber: position.lineNumber,
        endLineNumber: position.lineNumber,
        startColumn: position.column,
        endColumn: position.column,
      },
    };
  });
}

export function initJsonCodeLenses(commands: SwfMonacoEditorCommands): void {
  monaco.languages.registerCodeLensProvider("json", {
    provideCodeLenses: (model, cancellationToken) => {
      const rootNode = jsonc.parseTree(model.getValue());
      if (!rootNode) {
        return { lenses: [], dispose: () => {} };
      }

      return {
        lenses: [
          ...toLens(model, rootNode, ["functions"], ({ position }) => ({
            id: commands["FunctionsWidget"],
            title: `◎ Discover`,
            arguments: [{ position }],
          })),
          ...toLens(model, rootNode, ["functions"], ({ position }) => ({
            id: commands["FunctionsWidget"],
            title: `⤵ Import`,
            arguments: [{ position }],
          })),
          ...toLens(model, rootNode, ["functions"], ({ position }) => ({
            id: commands["FunctionsWidget"],
            title: `✎ Edit "functions"...`,
            arguments: [{ position }],
          })),
          ...toLens(
            model,
            rootNode,
            ["functions"],
            ({ position }) => ({
              id: commands["StatesWidget"],
              title: `+ Add function...`,
              arguments: [{ position }],
            }),
            "end"
          ),
          ...toLens(model, rootNode, ["states"], ({ position }) => ({
            id: commands["StatesWidget"],
            title: `✎ Edit "states"...`,
            arguments: [{ position }],
          })),
          ...toLens(
            model,
            rootNode,
            ["states"],
            ({ position }) => ({
              id: commands["StatesWidget"],
              title: `+ Add state...`,
              arguments: [{ position }],
            }),
            "end"
          ),
          ...toLens(model, rootNode, ["functions", "*", "name"], ({ position, node }) => ({
            id: commands["FunctionsWidget"],
            title: `↺ Rename '${node.value}'`,
            arguments: [{ position }],
          })),
          ...toLens(model, rootNode, ["states", "*", "name"], ({ position, node }) => ({
            id: commands["StatesWidget"],
            title: `↺ Rename '${node.value}'`,
            arguments: [{ position }],
          })),
          ...toLens(
            model,
            rootNode,
            ["states", "*", "actions"],
            ({ position }) => ({ id: commands["StatesWidget"], title: "+ Add action...", arguments: [{ position }] }),
            "end"
          ),
          ...toLens(model, rootNode, ["states", "*", "actions", "*", "name"], ({ position, node }) => ({
            id: commands["StatesWidget"],
            title: `Ⓧ Remove '${node.value}'`,
            arguments: [{ position }],
          })),
        ],

        dispose: () => {},
      };
    },
    resolveCodeLens(
      model: monaco.editor.ITextModel,
      codeLens: monaco.languages.CodeLens,
      cancellationToken: monaco.CancellationToken
    ): monaco.languages.ProviderResult<monaco.languages.CodeLens> {
      return codeLens;
    },
  });

  return;
}

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
