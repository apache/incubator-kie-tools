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
import { useContext, useMemo } from "react";
import { BeeGwtService, DmnDataType, ExpressionDefinition, PmmlDocument } from "../../api";
import { useRef, useState } from "react";
import "./BoxedExpressionEditorContext.css";
import { BoxedExpressionEditorProps } from "./BoxedExpressionEditor";
import { FeelVariables } from "@kie-tools/dmn-feel-antlr4-parser";

export interface BoxedExpressionEditorContextType {
  // Plumbing
  beeGwtService?: BeeGwtService;
  editorRef: React.RefObject<HTMLDivElement>;
  scrollableParentRef: React.RefObject<HTMLElement>;

  variables?: FeelVariables;

  // Props
  expressionHolderId: string;
  pmmlDocuments?: PmmlDocument[];
  dataTypes: DmnDataType[];

  // State
  currentlyOpenContextMenu: string | undefined;
  setCurrentlyOpenContextMenu: React.Dispatch<React.SetStateAction<string | undefined>>;

  widthsById: Map<string, number[]>;
}

export interface BoxedExpressionEditorDispatchContextType {
  setExpression: React.Dispatch<React.SetStateAction<ExpressionDefinition>>;
  setWidth: React.Dispatch<{ id: string; values: number[] }>;
}

export const BoxedExpressionEditorContext = React.createContext<BoxedExpressionEditorContextType>(
  {} as BoxedExpressionEditorContextType
);

export const BoxedExpressionEditorDispatchContext = React.createContext<BoxedExpressionEditorDispatchContextType>(
  {} as BoxedExpressionEditorDispatchContextType
);

export function useBoxedExpressionEditor() {
  return useContext(BoxedExpressionEditorContext);
}

export function useBoxedExpressionEditorDispatch() {
  return useContext(BoxedExpressionEditorDispatchContext);
}

export function BoxedExpressionEditorContextProvider({
  onExpressionChange,
  onWidthsChange,
  dataTypes,
  expressionHolderId,
  beeGwtService,
  children,
  pmmlDocuments,
  scrollableParentRef,
  variables,
  widthsById,
}: React.PropsWithChildren<BoxedExpressionEditorProps>) {
  const [currentlyOpenContextMenu, setCurrentlyOpenContextMenu] = useState<string | undefined>(undefined);

  const editorRef = useRef<HTMLDivElement>(null);

  const dispatch = useMemo<BoxedExpressionEditorDispatchContextType>(
    () => ({
      setExpression: onExpressionChange,
      setWidth: ({ id, values }) => {
        const n = new Map(widthsById);
        n.set(id, values);
        onWidthsChange(n);
      },
    }),
    [onExpressionChange, onWidthsChange, widthsById]
  );

  return (
    <BoxedExpressionEditorContext.Provider
      value={{
        //plumbing
        beeGwtService, // Move to a separate context?
        editorRef,
        scrollableParentRef,
        variables,

        // props
        expressionHolderId,
        dataTypes,
        pmmlDocuments,

        //state // FIXME: Move to a separate context (https://github.com/kiegroup/kie-issues/issues/168)
        currentlyOpenContextMenu,
        setCurrentlyOpenContextMenu,
        widthsById,
      }}
    >
      <BoxedExpressionEditorDispatchContext.Provider value={dispatch}>
        <div className="boxed-expression-provider" ref={editorRef}>
          {children}
        </div>
      </BoxedExpressionEditorDispatchContext.Provider>
    </BoxedExpressionEditorContext.Provider>
  );
}

export function NestedExpressionDispatchContextProvider({
  onSetExpression,
  children,
}: React.PropsWithChildren<{
  onSetExpression: (args: { getNewExpression: (prev: ExpressionDefinition) => ExpressionDefinition }) => void;
}>) {
  const { setWidth } = useBoxedExpressionEditorDispatch();
  const nestedExpressionDispatch = useMemo<BoxedExpressionEditorDispatchContextType>(() => {
    return {
      setExpression: (newExpressionAction: React.SetStateAction<ExpressionDefinition>) => {
        function getNewExpression(prev: ExpressionDefinition) {
          return typeof newExpressionAction === "function" ? newExpressionAction(prev) : newExpressionAction;
        }

        onSetExpression({ getNewExpression });
      },
      setWidth,
    };
  }, [onSetExpression, setWidth]);

  return (
    <BoxedExpressionEditorDispatchContext.Provider value={nestedExpressionDispatch}>
      {children}
    </BoxedExpressionEditorDispatchContext.Provider>
  );
}
