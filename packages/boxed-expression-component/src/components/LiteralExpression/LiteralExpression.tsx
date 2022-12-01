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
  DmnBuiltInDataType,
  executeIfExpressionDefinitionChanged,
  LiteralExpressionDefinition,
  ExpressionDefinitionLogicType,
  ExpressionDefinition,
} from "../../api";
import { ExpressionDefinitionHeaderMenu, EXPRESSION_NAME } from "../ExpressionDefinitionHeaderMenu";
import { Resizer } from "../Resizer";
import { BeeTableEditableCellContent } from "../BeeTable";
import { useBoxedExpressionEditor } from "../BoxedExpressionEditor/BoxedExpressionEditorContext";

const HEADER_WIDTH = 250;

export const LiteralExpression: React.FunctionComponent<LiteralExpressionDefinition> = (
  literalExpression: LiteralExpressionDefinition
) => {
  const { beeGwtService, decisionNodeId } = useBoxedExpressionEditor();

  const spreadLiteralExpressionDefinition = useCallback(
    (literalExpressionUpdate?: Partial<LiteralExpressionDefinition>) => {
      const expressionDefinition: LiteralExpressionDefinition = {
        id: literalExpression.id,
        name: literalExpression.name ?? EXPRESSION_NAME,
        dataType: literalExpression.dataType ?? DmnBuiltInDataType.Undefined,
        logicType: ExpressionDefinitionLogicType.LiteralExpression,
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
            beeGwtService?.broadcastLiteralExpressionDefinition?.(expressionDefinition);
          }
        },
        ["name", "dataType", "content", "width"]
      );
    },
    [beeGwtService, literalExpression]
  );

  const onExpressionHeaderUpdated = useCallback(
    ({
      dataType = DmnBuiltInDataType.Undefined,
      name = EXPRESSION_NAME,
    }: Pick<ExpressionDefinition, "name" | "dataType">) => {
      literalExpression.onExpressionHeaderUpdated?.({ name, dataType });
      spreadLiteralExpressionDefinition({ name, dataType });
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
      beeGwtService?.broadcastLiteralExpressionDefinition?.({
        ...literalExpression,
        logicType: ExpressionDefinitionLogicType.LiteralExpression,
      });
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const onHeaderClick = useCallback(() => {
    beeGwtService?.selectObject(decisionNodeId);
  }, [beeGwtService, decisionNodeId]);

  const onBodyClick = useCallback(() => {
    beeGwtService?.selectObject(literalExpression.id);
  }, [beeGwtService, literalExpression.id]);

  return (
    <div className="literal-expression">
      {!literalExpression.isHeadless && (
        <div className="literal-expression-header" onClick={onHeaderClick}>
          <Resizer
            width={literalExpression.width ?? HEADER_WIDTH}
            minWidth={HEADER_WIDTH}
            onHorizontalResizeStop={onHorizontalResizeStop}
          >
            <ExpressionDefinitionHeaderMenu
              selectedExpressionName={literalExpression.name ?? EXPRESSION_NAME}
              selectedDataType={literalExpression.dataType ?? DmnBuiltInDataType.Undefined}
              onExpressionHeaderUpdated={onExpressionHeaderUpdated}
            >
              <div className="expression-info">
                <p className="expression-name pf-u-text-truncate">{literalExpression.name ?? EXPRESSION_NAME}</p>
                <p className="expression-data-type pf-u-text-truncate">
                  ({literalExpression.dataType ?? DmnBuiltInDataType.Undefined})
                </p>
              </div>
            </ExpressionDefinitionHeaderMenu>
          </Resizer>
        </div>
      )}
      <div className={`${literalExpression.id} literal-expression-body`} onClick={onBodyClick}>
        <BeeTableEditableCellContent
          value={literalExpression.content ?? ""}
          rowIndex={0}
          columnId={literalExpression.id ?? "-"}
          onCellUpdate={onCellUpdate}
        />
      </div>
    </div>
  );
};
