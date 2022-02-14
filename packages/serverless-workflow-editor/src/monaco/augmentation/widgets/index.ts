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
  const node = jsonc.findNodeAtLocation(rootNode, jsonPath);
  const nodes = node ? [node] : [];

  return nodes.map((node) => {
    const position = model.getPositionAt(node.offset);

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

// export function findNodesAtLocation(root: jsonc.Node | undefined, path: JSONPath): jsonc.Node[] {
//   if (!root) {
//     return [];
//   }
//   const nodes = [];
//   let node = root;
//   for (const segment of path) {
//     if (typeof segment === "string") {
//       if (node.type !== "object" || !Array.isArray(node.children)) {
//         return [];
//       }
//       if (segment === "*") {
//         return nodes;
//       }
//       let found = false;
//       for (const propertyNode of node.children) {
//         if (
//           Array.isArray(propertyNode.children) &&
//           propertyNode.children[0].value === segment &&
//           propertyNode.children.length === 2
//         ) {
//           node = propertyNode.children[1];
//           found = true;
//           break;
//         }
//       }
//       if (!found) {
//         return [];
//       }
//     } else {
//       const index = <number>segment;
//       if (node.type !== "array" || index < 0 || !Array.isArray(node.children) || index >= node.children.length) {
//         return [];
//       }
//       node = node.children[index];
//     }
//   }
//   return nodes;
// }
