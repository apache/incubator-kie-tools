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
import { useMemo } from "react";
import { TextField, TextFieldType } from "../Fields";
import { BoxedExpressionIndex } from "../../boxedExpressions/boxedExpressionIndex";
import { useDmnEditorStore } from "../../store/StoreContext";
import { useBoxedExpressionUpdater } from "./useBoxedExpressionUpdater";
import { DMN15__tLiteralExpression } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { Normalized } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";

export function InvocationFunctionCallCell(props: {
  boxedExpressionIndex?: BoxedExpressionIndex;
  isReadOnly: boolean;
}) {
  const selectedObjectId = useDmnEditorStore((s) => s.boxedExpressionEditor.selectedObjectId);
  const selectedObjectInfos = useMemo(
    () => props.boxedExpressionIndex?.get(selectedObjectId ?? ""),
    [props.boxedExpressionIndex, selectedObjectId]
  );

  const updater = useBoxedExpressionUpdater<Normalized<DMN15__tLiteralExpression>>(
    selectedObjectInfos?.expressionPath ?? []
  );

  const cell = useMemo(
    () => selectedObjectInfos?.cell as Normalized<DMN15__tLiteralExpression>,
    [selectedObjectInfos?.cell]
  );

  return (
    <>
      <TextField
        type={TextFieldType.TEXT_INPUT}
        title={"Function to be called"}
        placeholder="Enter the function name..."
        isReadOnly={props.isReadOnly}
        initialValue={cell.text?.__$$text ?? ""}
        expressionPath={selectedObjectInfos?.expressionPath ?? []}
        onChange={(newDescription: string) =>
          updater((dmnObject) => {
            dmnObject.text = { __$$text: newDescription };
          })
        }
      />
    </>
  );
}
