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

import * as React from "react";
import { useCallback, useMemo } from "react";
import { TextInputField } from "./Fields";
import { BeeMap, ExpressionPath } from "../../boxedExpressions/getBeeMap";
import { useDmnEditorStore } from "../../store/Store";
import { useUpdateBee } from "./useUpdateBee";
import { DMN15__tLiteralExpression } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";

export function InvocationFunctionCallCell(props: { beeMap?: BeeMap; isReadonly: boolean }) {
  const { selectedObjectId } = useDmnEditorStore((s) => s.boxedExpressionEditor);
  const selectedObjectInfos = useMemo(
    () => props.beeMap?.get(selectedObjectId ?? ""),
    [props.beeMap, selectedObjectId]
  );

  const updateBee = useUpdateBee<DMN15__tLiteralExpression>(
    useCallback((dmnObject, newContent) => {
      if (newContent.text?.__$$text !== undefined) {
        dmnObject.text ??= { __$$text: "" };
        dmnObject.text = newContent.text as { __$$text: string };
      }
    }, []),
    props.beeMap
  );

  const cell = useMemo(() => selectedObjectInfos?.cell as DMN15__tLiteralExpression, [selectedObjectInfos?.cell]);

  return (
    <>
      <TextInputField
        title={"Function to be called"}
        placeholder="Enter the function name..."
        isReadonly={props.isReadonly}
        initialValue={cell.text?.__$$text ?? ""}
        expressionPath={selectedObjectInfos?.expressionPath ?? []}
        onChange={(newDescription: string, expressionPath: ExpressionPath[]) =>
          updateBee({ text: { __$$text: newDescription } }, expressionPath)
        }
      />
    </>
  );
}
