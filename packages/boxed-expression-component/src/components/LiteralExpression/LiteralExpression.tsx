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
import { useCallback, useMemo } from "react";
import { DmnBuiltInDataType, LiteralExpressionDefinition, ExpressionDefinition } from "../../api";
import { ExpressionDefinitionHeaderMenu, EXPRESSION_NAME } from "../ExpressionDefinitionHeaderMenu";
import { Resizer } from "../Resizer";
import { BeeTableEditableCellContent } from "../BeeTable";
import {
  useBoxedExpressionEditor,
  useBoxedExpressionEditorDispatch,
} from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { useNestedExpressionContainer } from "../ContextExpression";
import { ResizingWidth, useResizingWidthDispatch, useResizingWidths } from "../ExpressionDefinitionRoot";

export const LITERAL_EXPRESSION_MIN_WIDTH = 250;

// 14px for padding, 2px for border
export const LITERAL_EXPRESSION_EXTRA_WIDTH = 14 + 2; // 14px for margin, 2px for border

export function LiteralExpression(literalExpression: LiteralExpressionDefinition) {
  const { beeGwtService, decisionNodeId } = useBoxedExpressionEditor();
  const { setExpression } = useBoxedExpressionEditorDispatch();
  const nestedExpressionContainer = useNestedExpressionContainer();

  const onExpressionHeaderUpdated = useCallback(
    ({ dataType, name }: Pick<ExpressionDefinition, "name" | "dataType">) => {
      setExpression((prev) => ({ ...prev, name, dataType }));
    },
    [setExpression]
  );

  const setWidth = useCallback(
    (width) => {
      setExpression((prev) => ({ ...prev, width }));
    },
    [setExpression]
  );

  const updateContent = useCallback(
    (_number, _columnId, value) => {
      setExpression((prev) => ({ ...prev, content: value }));
    },
    [setExpression]
  );

  const selectDecisionNode = useCallback(() => {
    beeGwtService?.selectObject(decisionNodeId);
  }, [beeGwtService, decisionNodeId]);

  const selectLiteralExpression = useCallback(() => {
    beeGwtService?.selectObject(literalExpression.id);
  }, [beeGwtService, literalExpression.id]);

  const minWidthLocal = useMemo(() => {
    return Math.max(
      nestedExpressionContainer.minWidthLocal - LITERAL_EXPRESSION_EXTRA_WIDTH,
      LITERAL_EXPRESSION_MIN_WIDTH
    );
  }, [nestedExpressionContainer]);

  const minWidthGlobal = useMemo(() => {
    return Math.max(
      nestedExpressionContainer.minWidthGlobal - LITERAL_EXPRESSION_EXTRA_WIDTH,
      LITERAL_EXPRESSION_MIN_WIDTH
    );
  }, [nestedExpressionContainer]);

  //// RESIZING WIDTH

  const resizingWidthsDispatch = useResizingWidthDispatch();
  const { resizingWidths } = useResizingWidths();

  const setResizingWidth = useCallback(
    (getNewResizingWidth: (prev: ResizingWidth) => ResizingWidth) => {
      // FIXME: Tiago -> id optional
      resizingWidthsDispatch.updateResizingWidth(literalExpression.id!, getNewResizingWidth);
    },
    [literalExpression.id, resizingWidthsDispatch]
  );

  const resizingWidth = useMemo<ResizingWidth>(() => {
    return (
      // FIXME: Tiago -> id optional
      resizingWidths.get(literalExpression.id!) ?? {
        value: literalExpression.width ?? LITERAL_EXPRESSION_MIN_WIDTH,
        isPivoting: false,
      }
    );
  }, [literalExpression.id, literalExpression.width, resizingWidths]);

  React.useEffect(() => {
    setResizingWidth((prev) => ({
      value: literalExpression.width ?? LITERAL_EXPRESSION_MIN_WIDTH,
      isPivoting: false,
    }));
  }, [literalExpression.width, setResizingWidth]);

  React.useEffect(() => {
    setResizingWidth((prev) => {
      return prev.isPivoting
        ? {
            value: nestedExpressionContainer.resizingWidth.value - LITERAL_EXPRESSION_EXTRA_WIDTH,
            isPivoting: true,
          }
        : {
            value: Math.max(
              nestedExpressionContainer.resizingWidth.value - LITERAL_EXPRESSION_EXTRA_WIDTH,
              literalExpression.width ?? LITERAL_EXPRESSION_MIN_WIDTH
            ),
            isPivoting: false,
          };
    });
  }, [literalExpression.id, literalExpression.width, nestedExpressionContainer.resizingWidth, setResizingWidth]);

  return (
    <div className={`literal-expression ${resizingWidth.isPivoting ? "pivoting" : "not-pivoting"}`}>
      {!literalExpression.isHeadless && (
        <div className="literal-expression-header" onClick={selectDecisionNode}>
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
        </div>
      )}
      <div className={`${literalExpression.id} literal-expression-body`} onClick={selectLiteralExpression}>
        <Resizer
          width={literalExpression.width}
          minWidth={minWidthGlobal}
          setWidth={setWidth}
          actualWidth={literalExpression.width}
          resizingWidth={resizingWidth}
          setResizingWidth={setResizingWidth}
        >
          <BeeTableEditableCellContent
            value={`${literalExpression.id?.substring(0, 5)} - ${nestedExpressionContainer.resizingWidth.value}`}
            rowIndex={0}
            columnId={literalExpression.id ?? "-"}
            onCellUpdate={updateContent}
          />
        </Resizer>
      </div>
    </div>
  );
}
