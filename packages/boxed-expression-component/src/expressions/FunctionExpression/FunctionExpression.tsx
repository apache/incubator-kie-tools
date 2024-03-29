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

import _ from "lodash";
import * as React from "react";
import { useCallback, useMemo } from "react";
import { DmnBuiltInDataType, BoxedFunction, BoxedFunctionKind, generateUuid } from "../../api";
import { PopoverMenu } from "../../contextMenu/PopoverMenu";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { useBoxedExpressionEditor, useBoxedExpressionEditorDispatch } from "../../BoxedExpressionEditorContext";
import { FeelFunctionExpression, FeelFunctionProps } from "./FeelFunctionExpression";
import { FunctionKindSelector } from "./FunctionKindSelector";
import { JavaFunctionExpression, JavaFunctionProps } from "./JavaFunctionExpression";
import { ParametersPopover } from "./ParametersPopover";
import { PmmlFunctionExpression, PmmlFunctionProps } from "./PmmlFunctionExpression";
import {
  DMN15__tFunctionDefinition,
  DMN15__tFunctionKind,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import "./FunctionExpression.css";

export function FunctionExpression(
  functionExpression: BoxedFunction & {
    isNested: boolean;
    parentElementId: string;
  }
) {
  const functionKind = functionExpression["@_kind"] ?? "";
  switch (functionKind) {
    case BoxedFunctionKind.Feel:
      return <FeelFunctionExpression functionExpression={functionExpression as FeelFunctionProps} />;
    case BoxedFunctionKind.Java:
      return <JavaFunctionExpression functionExpression={functionExpression as JavaFunctionProps} />;
    case BoxedFunctionKind.Pmml:
      return <PmmlFunctionExpression functionExpression={functionExpression as PmmlFunctionProps} />;
    default:
      return <></>;
  }
}

export function useFunctionExpressionControllerCell(functionKind: DMN15__tFunctionKind) {
  const { setExpression } = useBoxedExpressionEditorDispatch();

  const onFunctionKindSelect = useCallback(
    (kind: DMN15__tFunctionKind) => {
      setExpression((prev) => {
        if (kind === BoxedFunctionKind.Feel) {
          return {
            __$$element: "functionDefinition",
            "@_label": prev["@_label"],
            "@_id": generateUuid(),
            "@_kind": BoxedFunctionKind.Feel,
            "@_typeRef": DmnBuiltInDataType.Undefined,
            expression: {
              __$$element: "literalExpression",
              id: generateUuid(),
              "@_typeRef": DmnBuiltInDataType.Undefined,
            },
            formalParameter: [],
          };
        } else if (kind === BoxedFunctionKind.Java) {
          const expressionId = generateUuid();
          return {
            __$$element: "functionDefinition",
            "@_label": prev["@_label"],
            "@_id": expressionId,
            expression: {
              __$$element: "context",
              id: generateUuid(),
            },
            "@_kind": BoxedFunctionKind.Java,
            "@_typeRef": DmnBuiltInDataType.Undefined,
            formalParameter: [],
          };
        } else if (kind === BoxedFunctionKind.Pmml) {
          return {
            __$$element: "functionDefinition",
            "@_label": prev["@_label"],
            "@_id": generateUuid(),
            expression: {
              __$$element: "context",
              id: generateUuid(),
            },
            "@_kind": BoxedFunctionKind.Pmml,
            "@_typeRef": DmnBuiltInDataType.Undefined,
            formalParameter: [],
          };
        } else {
          throw new Error("Shouldn't ever reach this point.");
        }
      });
    },
    [setExpression]
  );

  return useMemo(
    () => <FunctionKindSelector selectedFunctionKind={functionKind} onFunctionKindSelect={onFunctionKindSelect} />,
    [functionKind, onFunctionKindSelect]
  );
}

export function useFunctionExpressionParametersColumnHeader(
  formalParameters: DMN15__tFunctionDefinition["formalParameter"]
) {
  const { i18n } = useBoxedExpressionEditorI18n();

  const { editorRef } = useBoxedExpressionEditor();

  return useMemo(
    () => (
      <PopoverMenu
        appendTo={() => editorRef.current!}
        className="parameters-editor-popover"
        minWidth="400px"
        body={<ParametersPopover parameters={formalParameters ?? []} />}
      >
        <div className={`parameters-list ${_.isEmpty(formalParameters) ? "empty-parameters" : ""}`}>
          <p className="pf-u-text-truncate">
            {_.isEmpty(formalParameters) ? (
              i18n.editParameters
            ) : (
              <>
                <span>{"("}</span>
                {(formalParameters ?? []).map((parameter, i) => (
                  <React.Fragment key={i}>
                    <span>{parameter["@_name"]}</span>
                    <span>{": "}</span>
                    <span className={"expression-info-data-type"}>({parameter["@_typeRef"]})</span>
                    {i < (formalParameters ?? []).length - 1 && <span>{", "}</span>}
                  </React.Fragment>
                ))}
                <span>{")"}</span>
              </>
            )}
          </p>
        </div>
      </PopoverMenu>
    ),
    [formalParameters, i18n.editParameters, editorRef]
  );
}
