/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { SwfRef, swfRefValidationMap } from "./swfRefValidationMap";
import { findNodesAtLocation } from "./findNodesAtLocation";
import { SwfLsNode } from "./types";
import { TextDocument } from "vscode-languageserver-textdocument";
import { Diagnostic } from "vscode-languageserver-types";

export function doRefValidation(args: { textDocument: TextDocument; rootNode: SwfLsNode }): Diagnostic[] {
  return [...swfRefValidationMap.entries()].flatMap(([src, refs]) => {
    // here, we assume that all source nodes return terminal values.
    // i.e. a source node will never be an "object"
    const sourceNodeValues = new Set(
      findNodesAtLocation({ root: args.rootNode, path: src.path })
        .filter((node) => node?.type === "string")
        .flatMap((node) => node.value)
    );

    return refs.flatMap((ref) =>
      // find terminal nodes that are refs
      findNodesAtLocation({ root: args.rootNode, path: ref.path })
        .flatMap((refNode) => {
          // include this node if types match
          if (refNode.type === ref.type) {
            return [refNode];
          }

          // include all children nodes if array types match
          if (areArraysOfMatchingType({ ref, refNode })) {
            return refNode.children ?? [];
          }

          // ignore as type mismatches are validated by the JSON Schema
          return [];
        })
        .flatMap((refNode) => {
          if (sourceNodeValues.has(refNode.value)) {
            // if ref matches a source value, ignore it, as it's correct
            return [];
          }

          // if not, return a diagnostic
          return [
            {
              message: `Missing '${refNode.value}' in '${src.name}'`,
              range: {
                start: args.textDocument.positionAt(refNode.offset),
                end: args.textDocument.positionAt(refNode.offset + refNode.length),
              },
            },
          ];
        })
    );
  });
}

function areArraysOfMatchingType(args: { ref: SwfRef; refNode: SwfLsNode }) {
  return (
    args.ref.isArray &&
    args.refNode.type === "array" &&
    args.refNode.children?.every((child) => child.type === args.ref.type)
  );
}
