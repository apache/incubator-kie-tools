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
import { useCallback } from "react";
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

const HEADER_WIDTH = 250;

export const LiteralExpression: React.FunctionComponent<LiteralExpressionProps> = (
  literalExpression: LiteralExpressionProps
) => {
  const spreadLiteralExpressionDefinition = useCallback(
    (literalExpressionUpdate?: Partial<LiteralExpressionProps>) => {
      const expressionDefinition: LiteralExpressionProps = {
        uid: literalExpression.uid,
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
            window.beeApi?.broadcastLiteralExpressionDefinition?.(expressionDefinition);
          }
        },
        ["name", "dataType", "content", "width"]
      );
    },
    [literalExpression]
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

  const onContentChange = useCallback(
    (content: string) => {
      spreadLiteralExpressionDefinition({
        content,
      });
    },
    [spreadLiteralExpressionDefinition]
  );

  const onHorizontalResizeStop = useCallback(
    (width) => {
      spreadLiteralExpressionDefinition({
        width,
      });
    },
    [spreadLiteralExpressionDefinition]
  );

  return (
    <div className="literal-expression">
      {!literalExpression.isHeadless && (
        <div className="literal-expression-header">
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
      <div className="literal-expression-body">
        <EditableCell
          value={literalExpression.content ?? ""}
          rowIndex={0}
          columnId={literalExpression.uid ?? "-"}
          onCellUpdate={(_number, _columnId, value) => onContentChange(value)}
        />
      </div>
    </div>
  );
};
