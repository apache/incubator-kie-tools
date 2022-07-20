import * as jsonc from "jsonc-parser";
import { sourcePaths, targetPaths } from "./validationPaths";
import { findNodesAtLocation } from "./findNodesAtLocation";
import { Position, TextDocument } from "vscode-json-languageservice";
import { SwfLsNode } from "./types";

export type TargetPathType = {
  path: string[];
  type: "string" | "string[]" | "number" | "boolean";
};

export type TargetValueType = {
  length: number;
  offset: number;
  value: string;
};

export type validationResultType = {
  range: {
    start: Position;
    end: Position;
  };
  message: string;
};

export const doCustomValidation = (content: string, textDocument: TextDocument) => {
  const rootNode = jsonc.parseTree(content);

  if (!rootNode) {
    return [];
  }
  const customValidationResults: any[] = [];

  sourcePaths.forEach((value: string[], key: string) => {
    const sourceValues = sourcePathValues(rootNode, value);
    const targetElements = targetPaths.get(key);
    if (!targetElements) {
      return [];
    }
    const collection = targetElements.flatMap((targetElement: TargetPathType) => {
      return targetPathValues(textDocument, sourceValues, rootNode, targetElement, key);
    });
    collection.filter((element) => {
      if (element) {
        customValidationResults.push(element);
      }
    });
  });
  return customValidationResults;
};

function isArrayOfStrings(value: jsonc.Node) {
  if (value.type === "array" && value?.children?.every((item: any) => typeof item.value === "string")) {
    return "string[]";
  }
}

const targetPathValues = (
  textDocument: TextDocument,
  sourceValues: string[],
  rootNode: SwfLsNode | undefined,
  targetElement: TargetPathType,
  nodeElement: string
) => {
  const targetNodes = findNodesAtLocation(rootNode, targetElement.path);

  const targetValues = targetNodes.flatMap((targetNode: jsonc.Node) => {
    let targetValue: TargetValueType = {} as TargetValueType;
    const targetArr: TargetValueType[] = [];
    if (targetNode.type === targetElement.type) {
      targetValue = {
        length: targetNode.length,
        offset: targetNode.offset,
        value: targetNode.value,
      };
      targetArr.push(targetValue);
    } else if (targetElement.type === isArrayOfStrings(targetNode)) {
      targetNode.children?.forEach((child: jsonc.Node) => {
        targetValue = {
          length: child.length,
          offset: child.offset,
          value: child.value,
        };
        targetArr.push(targetValue);
      });
    }
    return targetArr;
  });

  return compareValues(textDocument, sourceValues, targetValues, nodeElement);
};

const compareValues = (
  textDocument: TextDocument,
  sourceValues: string[],
  targetValues: TargetValueType[],
  nodeElement: string
) => {
  return targetValues.map((targetValue: TargetValueType) => {
    if (sourceValues.indexOf(targetValue.value) === -1) {
      const errObj = {
        message: `Missing ${targetValue.value} in ${nodeElement}`,
        range: {
          start: textDocument.positionAt(targetValue.offset),
          end: textDocument.positionAt(targetValue.offset + targetValue.length),
        },
      };
      return errObj;
    }
  });
};

const sourcePathValues = (rootNode: SwfLsNode | undefined, path: string[]) => {
  const sourceNodes = findNodesAtLocation(rootNode, path);
  const sourceValues = sourceNodes.flatMap((sourceNode: jsonc.Node) => {
    return jsonc.getNodeValue(sourceNode);
  });
  return sourceValues;
};
