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
  id: string,
  commandDelegate: (args: { position: Position }) => CodeLens["command"]
) {
  const nodes = findNodesAtLocation(rootNode, jsonPath);
  return nodes.map((node) => {
    const position = model.getPositionAt(node.parent!.offset);

    return {
      id,
      command: commandDelegate({ position }),
      range: {
        startLineNumber: position.lineNumber,
        endLineNumber: position.lineNumber,
        startColumn: position.column,
        endColumn: position.column,
      },
    };
  });
}

export function initJsonWidgets(commands: SwfMonacoEditorCommands): void {
  monaco.languages.registerCodeLensProvider("json", {
    provideCodeLenses: (model, cancellationToken) => {
      const rootNode = jsonc.parseTree(model.getValue());
      if (!rootNode) {
        return { lenses: [], dispose: () => {} };
      }

      return {
        lenses: [
          ...toLens(model, rootNode, ["functions"], "Edit functions", ({ position }) => ({
            id: commands["FunctionsWidget"],
            title: "Edit Functions",
            arguments: [{ position }],
          })),
          ...toLens(model, rootNode, ["states"], "Edit states", ({ position }) => ({
            id: commands["StatesWidget"],
            title: "Edit States",
            arguments: [{ position }],
          })),
          ...toLens(model, rootNode, ["functions", "*", "name"], "Rename", ({ position }) => ({
            id: commands["FunctionsWidget"],
            title: "Rename",
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
