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
import { useCallback, useEffect, useMemo, useState } from "react";
import { DmnBuiltInDataType, LiteralExpressionDefinition, ExpressionDefinition } from "../../api";
import { ExpressionDefinitionHeaderMenu, EXPRESSION_NAME } from "../ExpressionDefinitionHeaderMenu";
import { Resizer } from "../../resizing/Resizer";
import { BeeTableEditableCellContent } from "../../table/BeeTable";
import {
  useBoxedExpressionEditor,
  useBoxedExpressionEditorDispatch,
} from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { ResizingWidth, useResizingWidthsDispatch, useResizingWidths } from "../../resizing/ResizingWidthsContext";
import { useNestedExpressionContainer } from "../../resizing/NestedExpressionContainerContext";
import { LITERAL_EXPRESSION_MIN_WIDTH, NESTED_EXPRESSION_RESET_MARGIN } from "../../resizing/WidthValues";

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
    (value: string) => {
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

  //// RESIZING WIDTH

  const minWidthGlobal = useMemo(() => {
    return Math.max(
      nestedExpressionContainer.minWidthGlobal - NESTED_EXPRESSION_RESET_MARGIN,
      LITERAL_EXPRESSION_MIN_WIDTH
    );
  }, [nestedExpressionContainer]);

  const { updateResizingWidth } = useResizingWidthsDispatch();
  const { resizingWidths } = useResizingWidths();

  const setResizingWidth = useCallback(
    (getNewResizingWidth: (prev: ResizingWidth) => ResizingWidth) => {
      // FIXME: Tiago -> id optional
      updateResizingWidth(literalExpression.id!, getNewResizingWidth);
    },
    [literalExpression.id, updateResizingWidth]
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

  useEffect(() => {
    setResizingWidth((prev) => ({
      value: literalExpression.width ?? LITERAL_EXPRESSION_MIN_WIDTH,
      isPivoting: false,
    }));
  }, [literalExpression.width, setResizingWidth]);

  useEffect(() => {
    setResizingWidth((prev) => {
      return prev.isPivoting
        ? {
            value: Math.max(
              nestedExpressionContainer.resizingWidth.value - NESTED_EXPRESSION_RESET_MARGIN,
              minWidthGlobal
            ),
            isPivoting: true,
          }
        : {
            value: Math.max(
              nestedExpressionContainer.resizingWidth.value - NESTED_EXPRESSION_RESET_MARGIN,
              minWidthGlobal,
              literalExpression.width ?? LITERAL_EXPRESSION_MIN_WIDTH
            ),
            isPivoting: false,
          };
    });
  }, [
    literalExpression.id,
    literalExpression.width,
    minWidthGlobal,
    nestedExpressionContainer.minWidthGlobal,
    nestedExpressionContainer.resizingWidth,
    setResizingWidth,
  ]);

  //

  const [isEditing, setEditing] = useState(false);

  const onDoubleClick = useCallback((e: React.MouseEvent) => {
    e.stopPropagation();
    setEditing(true);
  }, []);

  return (
    <div className={`literal-expression`}>
      {!literalExpression.isHeadless && (
        <div className="literal-expression-header" onClick={selectDecisionNode}>
          <ExpressionDefinitionHeaderMenu
            selectedExpressionName={literalExpression.name ?? EXPRESSION_NAME}
            selectedDataType={literalExpression.dataType ?? DmnBuiltInDataType.Undefined}
            onExpressionHeaderUpdated={onExpressionHeaderUpdated}
          >
            <div className={"expression-info"}>
              <p className="expression-info-name pf-u-text-truncate">{literalExpression.name ?? EXPRESSION_NAME}</p>
              <p className="expression-info-data-type pf-u-text-truncate">({literalExpression.dataType})</p>
            </div>
          </ExpressionDefinitionHeaderMenu>
        </div>
      )}
      <div
        className={`${literalExpression.id} literal-expression-body ${isEditing ? "editing" : ""}`}
        onClick={selectLiteralExpression}
        onDoubleClick={onDoubleClick}
        style={{ width: resizingWidth.value, minWidth: minWidthGlobal }}
      >
        <BeeTableEditableCellContent
          isReadOnly={false}
          value={literalExpression.content ?? ""}
          onChange={updateContent}
          isActive={isEditing}
          isEditing={isEditing}
          setEditing={setEditing}
        />
        <Resizer
          minWidth={minWidthGlobal}
          width={literalExpression.width}
          setWidth={setWidth}
          resizingWidth={resizingWidth}
          setResizingWidth={setResizingWidth}
        />
      </div>
    </div>
  );
}
