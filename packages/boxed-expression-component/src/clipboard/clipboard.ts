/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { BoxedExpression } from "../api";
import { findAllIdsDeep } from "../ids/ids";

export const DMN_BOXED_EXPRESSION_CLIPBOARD_MIME_TYPE =
  "application/json+kie-dmn-boxed-expression-editor--expression" as const;

export type BoxedExpressionClipboard = {
  mimeType: typeof DMN_BOXED_EXPRESSION_CLIPBOARD_MIME_TYPE;
  expression: BoxedExpression;
  widthsById: Record<string, number[]>;
};

export function buildClipboardFromExpression(
  expression: BoxedExpression,
  widthsById: Map<string, number[]>
): BoxedExpressionClipboard {
  const originalIds = findAllIdsDeep(expression);

  const widthsByIdObj = [...widthsById.entries()].reduce<Record<string, number[]>>((acc, [k, v]) => {
    if (originalIds.has(k)) {
      acc[k] = v;
    }
    return acc;
  }, {});

  return {
    mimeType: DMN_BOXED_EXPRESSION_CLIPBOARD_MIME_TYPE,
    expression,
    widthsById: widthsByIdObj,
  };
}
