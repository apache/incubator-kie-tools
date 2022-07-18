import * as jsonc from "jsonc-parser";
import { sourcePaths, targetPaths } from "./validationPaths";
import { findNodesAtLocation, SwfLsNode } from "./SwfLanguageUtilMethods";
import { Position, TextDocument } from "vscode-json-languageservice";

export type TargetPathType = {
  path: string[];
  type: string;
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
  const customValidationResults: validationResultType[] = [];

  sourcePaths.forEach((value: string[], key: string) => {
    const source = sourcePathValues(rootNode, value);
    const targetElements = targetPaths.get(key);
    targetElements.forEach((targetElement: TargetPathType) => {
      targetPathValues(textDocument, customValidationResults, source, rootNode, targetElement, key);
    });
  });

  return customValidationResults;
};

const targetPathValues = (
  textDocument: TextDocument,
  customValidationResults: validationResultType[],
  source: any,
  rootNode: SwfLsNode | undefined,
  targetElement: TargetPathType,
  key: string
) => {
  const targetNodes = findNodesAtLocation(rootNode, targetElement.path);

  const targetValues = targetNodes.flatMap((targetNode: any) => {
    let temp: any = {};
    const targetArr: any = [];
    if (targetNode.type === "string") {
      temp = {
        length: targetNode.length,
        offset: targetNode.offset,
        value: targetNode.value,
      };
      targetArr.push(temp);
    } else if (targetNode.type === "array") {
      targetNode.children?.forEach((child: any) => {
        temp = {
          length: child.length,
          offset: child.offset,
          value: child.value,
        };
        targetArr.push(temp);
      });
    }
    return targetArr;
  });
  compareValues(textDocument, customValidationResults, source, targetValues.flat(), key);
};

const compareValues = (
  textDocument: TextDocument,
  customValidationResults: validationResultType[],
  source: any,
  target: any,
  key: string
) => {
  target.forEach((ele: any) => {
    if (typeof ele.value === "string") {
      if (source.indexOf(ele.value) === -1) {
        const errObj = {
          message: `Missing ${ele.value} in ${key}`,
          range: {
            start: textDocument.positionAt(ele.offset),
            end: textDocument.positionAt(ele.offset + ele.length),
          },
        };
        customValidationResults.push(errObj);
      }
    } else if (typeof ele.value === "object") {
      if (source.indexOf(ele.value?.refName) === -1) {
        const errObj = {
          message: `Missing ${ele.value} in ${key}`,
          range: {
            start: textDocument.positionAt(ele.offset),
            end: textDocument.positionAt(ele.offset + ele.length),
          },
        };
        customValidationResults.push(errObj);
      } else {
        if (source.indexOf(ele.value[0]) === -1) {
          const errObj = {
            message: `Missing ${ele.value} in ${key}`,
            range: {
              start: textDocument.positionAt(ele.offset),
              end: textDocument.positionAt(ele.offset + ele.length),
            },
          };
          customValidationResults.push(errObj);
        }
      }
    }
  });
};

const sourcePathValues = (rootNode: SwfLsNode | undefined, path: string[]) => {
  const sourceNodes = findNodesAtLocation(rootNode, path);
  const sourceFunctions: string[] = [];
  sourceNodes.flatMap((sourceNode: any) => {
    sourceFunctions.push(jsonc.getNodeValue(sourceNode));
  });
  return sourceFunctions;
};
