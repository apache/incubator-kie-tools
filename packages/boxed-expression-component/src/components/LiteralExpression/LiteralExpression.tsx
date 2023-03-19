/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import "./LiteralExpression.css";
import * as React from "react";
import { useCallback, useEffect } from "react";
import {
  DataType,
  executeIfExpressionDefinitionChanged,
  ExpressionProps,
  LiteralExpressionProps,
  LogicType,
} from "../../api";
import { EditExpressionMenu, EXPRESSION_NAME } from "../EditExpressionMenu";
import { Resizer } from "../Resizer";
import { EditableCell } from "../Table";
import { useBoxedExpression } from "../../context";

const HEADER_WIDTH = 250;

export const LiteralExpression: React.FunctionComponent<LiteralExpressionProps> = (
  literalExpression: LiteralExpressionProps
) => {
  const { boxedExpressionEditorGWTService, decisionNodeId } = useBoxedExpression();

  const spreadLiteralExpressionDefinition = useCallback(
    (literalExpressionUpdate?: Partial<LiteralExpressionProps>) => {
      const expressionDefinition: LiteralExpressionProps = {
        id: literalExpression.id,
        name: literalExpression.name ?? EXPRESSION_NAME,
        dataType: literalExpression.dataType ?? DataType.Undefined,
        logicType: LogicType.LiteralExpression,
        content: literalExpression.content ?? "",
        ...(!literalExpression.isHeadless && literalExpression.width !== HEADER_WIDTH
          ? { width: literalExpression.width }
          : {}),
        ...literalExpressionUpdate,
      };

      executeIfExpressionDefinitionChanged(
        literalExpression,
        expressionDefinition,
        () => {
          if (literalExpression.isHeadless) {
            literalExpression.onUpdatingRecursiveExpression?.(expressionDefinition);
          } else {
            boxedExpressionEditorGWTService?.broadcastLiteralExpressionDefinition?.(expressionDefinition);
          }
        },
        ["name", "dataType", "content", "width"]
      );
    },
    [boxedExpressionEditorGWTService, literalExpression]
  );

  const onExpressionUpdate = useCallback(
    ({ dataType = DataType.Undefined, name = EXPRESSION_NAME }: ExpressionProps) => {
      literalExpression.onUpdatingNameAndDataType?.(name, dataType);
      spreadLiteralExpressionDefinition({
        name,
        dataType,
      });
    },
    [literalExpression, spreadLiteralExpressionDefinition]
  );

  const onHorizontalResizeStop = useCallback(
    (width) => {
      spreadLiteralExpressionDefinition({
        width,
      });
    },
    [spreadLiteralExpressionDefinition]
  );

  const onCellUpdate = useCallback(
    (_number, _columnId, value) => {
      spreadLiteralExpressionDefinition({
        content: value,
      });
    },
    [spreadLiteralExpressionDefinition]
  );

  // TODO: https://issues.redhat.com/browse/KOGITO-6341
  useEffect(() => {
    if (!literalExpression.isHeadless) {
      boxedExpressionEditorGWTService?.broadcastLiteralExpressionDefinition?.({
        ...literalExpression,
        logicType: LogicType.LiteralExpression,
      });
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const onHeaderClick = useCallback(() => {
    boxedExpressionEditorGWTService?.selectObject(decisionNodeId);
  }, [boxedExpressionEditorGWTService, decisionNodeId]);

  const onBodyClick = useCallback(() => {
    boxedExpressionEditorGWTService?.selectObject(literalExpression.id);
  }, [boxedExpressionEditorGWTService, literalExpression.id]);

  return (
    <div className="literal-expression">
      {!literalExpression.isHeadless && (
        <div className="literal-expression-header" onClick={onHeaderClick}>
          <Resizer
            width={literalExpression.width ?? HEADER_WIDTH}
            minWidth={HEADER_WIDTH}
            onHorizontalResizeStop={onHorizontalResizeStop}
          >
            <EditExpressionMenu
              selectedExpressionName={literalExpression.name ?? EXPRESSION_NAME}
              selectedDataType={literalExpression.dataType ?? DataType.Undefined}
              onExpressionUpdate={onExpressionUpdate}
            >
              <div className="expression-info">
                <p className="expression-name pf-u-text-truncate">{literalExpression.name ?? EXPRESSION_NAME}</p>
                <p className="expression-data-type pf-u-text-truncate">
                  ({literalExpression.dataType ?? DataType.Undefined})
                </p>
              </div>
            </EditExpressionMenu>
          </Resizer>
        </div>
      )}
      <div className={`${literalExpression.id} literal-expression-body`} onClick={onBodyClick}>
        <EditableCell
          value={literalExpression.content ?? ""}
          rowIndex={0}
          columnId={literalExpression.id ?? "-"}
          onCellUpdate={onCellUpdate}
        />
      </div>
    </div>
  );
};
