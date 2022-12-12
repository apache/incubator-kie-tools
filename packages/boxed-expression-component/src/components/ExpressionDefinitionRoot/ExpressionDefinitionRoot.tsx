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

import * as React from "react";
import { useCallback, useMemo, useRef, useState } from "react";
import {
  CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH,
  ExpressionDefinition,
  ExpressionDefinitionLogicType,
  generateUuid,
} from "../../api";
import { useContextMenuHandler } from "../../hooks";
import { useBoxedExpressionEditorDispatch } from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { getDefaultExpressionDefinitionByLogicType } from "../ContextExpression";
import { ExpressionDefinitionLogicTypeSelector } from "../ExpressionDefinitionLogicTypeSelector";
import "./ExpressionDefinitionRoot.css";

export interface ExpressionDefinitionRootProps {
  decisionNodeId: string;
  expression: ExpressionDefinition;
}

export function ExpressionDefinitionRoot({ decisionNodeId, expression }: ExpressionDefinitionRootProps) {
  const expressionContainerRef = useRef<HTMLDivElement>(null);

  const { setExpression } = useBoxedExpressionEditorDispatch();

  const onLogicTypeSelected = useCallback(
    (logicType) => {
      return setExpression((prev) => {
        {
          return {
            ...getDefaultExpressionDefinitionByLogicType(
              logicType,
              { resizingWidth: CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH, isPivoting: false },
              prev
            ),
            logicType,
            isHeadless: false,
            id: prev.id ?? generateUuid(),
          };
        }
      });
    },
    [setExpression]
  );

  const onLogicTypeReset = useCallback(() => {
    setExpression((prev) => ({
      id: prev.id,
      name: prev.name,
      dataType: prev.dataType,
      logicType: ExpressionDefinitionLogicType.Undefined,
    }));
  }, [setExpression]);

  const getLogicTypeSelectorRef = useCallback(() => expressionContainerRef.current!, []);

  return (
    <ResizingWidthContextProvider>
      <div className="expression-container">
        <div className="expression-name-and-logic-type">
          <span className="expression-title">{expression.name}</span>
          &nbsp;
          <span className="expression-type">({expression.logicType})</span>
        </div>

        <div
          className={`expression-container-box ${decisionNodeId}`}
          ref={expressionContainerRef}
          data-ouia-component-id="expression-container"
        >
          <ExpressionDefinitionLogicTypeSelector
            expression={expression}
            onLogicTypeSelected={onLogicTypeSelected}
            onLogicTypeReset={onLogicTypeReset}
            getPlacementRef={getLogicTypeSelectorRef}
          />
        </div>
      </div>
    </ResizingWidthContextProvider>
  );
}

export function ResizingWidthContextProvider({ children }: React.PropsWithChildren<{}>) {
  const [resizingWidths, setResizingWidths] = useState<ResizingWidthContextType["resizingWidths"]>(new Map());

  const value = useMemo(() => {
    return { resizingWidths };
  }, [resizingWidths]);

  const dispatch = useMemo<ResizingWidthDispatchContextType>(() => {
    return {
      updateResizingWidth: (id, update) => {
        setResizingWidths((prev) => {
          const prevCopy = new Map(prev);
          const newValue = update(prevCopy.get(id));
          prevCopy.set(id, newValue);
          return prevCopy;
        });
      },
    };
  }, []);

  return (
    <ResizingWidthContext.Provider value={value}>
      <ResizingWidthDispatchContext.Provider value={dispatch}>
        <>{children}</>
      </ResizingWidthDispatchContext.Provider>
    </ResizingWidthContext.Provider>
  );
}

export type ResizingWidth = { resizingWidth: number; isPivoting: boolean };

export type ResizingWidthContextType = {
  resizingWidths: Map<string, ResizingWidth>;
};

export type ResizingWidthDispatchContextType = {
  updateResizingWidth(id: string, getNewResizingWidth: (prev: ResizingWidth | undefined) => ResizingWidth): void;
};

const ResizingWidthContext = React.createContext({} as ResizingWidthContextType);
const ResizingWidthDispatchContext = React.createContext({} as ResizingWidthDispatchContextType);

export function useResizingWidths() {
  return React.useContext(ResizingWidthContext);
}

export function useResizingWidthDispatch() {
  return React.useContext(ResizingWidthDispatchContext);
}
