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
import {
  DmnBuiltInDataType,
  ExpressionDefinitionLogicType,
  FunctionExpressionDefinition,
  FunctionExpressionDefinitionKind,
  generateUuid,
} from "../../api";
import { PopoverMenu } from "../../contextMenu/PopoverMenu";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { JAVA_FUNCTION_EXPRESSION_VALUES_MIN_WIDTH } from "../../resizing/WidthConstants";
import {
  useBoxedExpressionEditor,
  useBoxedExpressionEditorDispatch,
} from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { assertUnreachable } from "../ExpressionDefinitionRoot/ExpressionDefinitionLogicTypeSelector";
import { FeelFunctionExpression } from "./FeelFunctionExpression";
import "./FunctionExpression.css";
import { FunctionKindSelector } from "./FunctionKindSelector";
import { JavaFunctionExpression } from "./JavaFunctionExpression";
import { ParametersPopover } from "./ParametersPopover";
import { PmmlFunctionExpression } from "./PmmlFunctionExpression";

export const DEFAULT_FIRST_PARAM_NAME = "p-1";

export function FunctionExpression(
  functionExpression: FunctionExpressionDefinition & { isNested: boolean; parentElementId: string }
) {
  const functionKind = functionExpression.functionKind;
  switch (functionKind) {
    case FunctionExpressionDefinitionKind.Feel:
      return <FeelFunctionExpression functionExpression={functionExpression} />;
    case FunctionExpressionDefinitionKind.Java:
      return <JavaFunctionExpression functionExpression={functionExpression} />;
    case FunctionExpressionDefinitionKind.Pmml:
      return <PmmlFunctionExpression functionExpression={functionExpression} />;
    default:
      assertUnreachable(functionKind);
  }
}

export function useFunctionExpressionControllerCell(functionKind: FunctionExpressionDefinitionKind) {
  const { setExpression } = useBoxedExpressionEditorDispatch();

  const onFunctionKindSelect = useCallback(
    (kind: string) => {
      setExpression((prev) => {
        if (kind === FunctionExpressionDefinitionKind.Feel) {
          return {
            name: prev.name,
            id: generateUuid(),
            logicType: ExpressionDefinitionLogicType.Function,
            functionKind: FunctionExpressionDefinitionKind.Feel,
            dataType: DmnBuiltInDataType.Undefined,
            expression: {
              id: generateUuid(),
              logicType: ExpressionDefinitionLogicType.Undefined,
              dataType: DmnBuiltInDataType.Undefined,
            },
            formalParameters: [],
          };
        } else if (kind === FunctionExpressionDefinitionKind.Java) {
          return {
            name: prev.name,
            id: generateUuid(),
            logicType: ExpressionDefinitionLogicType.Function,
            functionKind: FunctionExpressionDefinitionKind.Java,
            dataType: DmnBuiltInDataType.Undefined,
            classAndMethodNamesWidth: JAVA_FUNCTION_EXPRESSION_VALUES_MIN_WIDTH,
            formalParameters: [],
          };
        } else if (kind === FunctionExpressionDefinitionKind.Pmml) {
          return {
            name: prev.name,
            id: generateUuid(),
            logicType: ExpressionDefinitionLogicType.Function,
            functionKind: FunctionExpressionDefinitionKind.Pmml,
            dataType: DmnBuiltInDataType.Undefined,
            formalParameters: [],
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
  formalParameters: FunctionExpressionDefinition["formalParameters"]
) {
  const { i18n } = useBoxedExpressionEditorI18n();

  const { editorRef } = useBoxedExpressionEditor();

  return useMemo(
    () => (
      <PopoverMenu
        appendTo={() => editorRef.current!}
        className="parameters-editor-popover"
        minWidth="400px"
        body={<ParametersPopover parameters={formalParameters} />}
      >
        <div className={`parameters-list ${_.isEmpty(formalParameters) ? "empty-parameters" : ""}`}>
          <p className="pf-u-text-truncate">
            {_.isEmpty(formalParameters) ? (
              i18n.editParameters
            ) : (
              <>
                <span>{"("}</span>
                {formalParameters.map((parameter, i) => (
                  <React.Fragment key={i}>
                    <span>{parameter.name}</span>
                    <span>{": "}</span>
                    <span className={"expression-info-data-type"}>({parameter.dataType})</span>
                    {i < formalParameters.length - 1 && <span>{", "}</span>}
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
