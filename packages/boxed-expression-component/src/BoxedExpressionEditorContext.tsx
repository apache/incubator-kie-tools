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
import { BeeGwtService, DmnDataType, BoxedExpression, PmmlDocument } from "./api";
import { useRef, useState } from "react";
import { BoxedExpressionEditorProps, OnRequestFeelVariables } from "./BoxedExpressionEditor";
import "./BoxedExpressionEditorContext.css";

export interface BoxedExpressionEditorContextType {
  // Plumbing
  beeGwtService?: BeeGwtService;
  editorRef: React.RefObject<HTMLDivElement>;
  scrollableParentRef: React.RefObject<HTMLElement>;

  // Props
  expressionHolderId: string;
  pmmlDocuments?: PmmlDocument[];
  dataTypes: DmnDataType[];

  // State
  currentlyOpenContextMenu: string | undefined;
  setCurrentlyOpenContextMenu: React.Dispatch<React.SetStateAction<string | undefined>>;

  onRequestFeelVariables?: OnRequestFeelVariables;
  widthsById: Map<string, number[]>;
  hideDmn14BoxedExpressions?: boolean;
}

export interface BoxedExpressionEditorDispatchContextType {
  setExpression: React.Dispatch<React.SetStateAction<BoxedExpression>>;
  setWidthsById: (mutation: ({ newMap }: { newMap: Map<string, number[]> }) => void) => void;
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
  onRequestFeelVariables,
  widthsById,
  hideDmn14BoxedExpressions,
}: React.PropsWithChildren<BoxedExpressionEditorProps>) {
  const [currentlyOpenContextMenu, setCurrentlyOpenContextMenu] = useState<string | undefined>(undefined);

  const editorRef = useRef<HTMLDivElement>(null);

  const widthsByIdRef = useRef<Map<string, number[]>>(widthsById);
  React.useEffect(() => {
    widthsByIdRef.current = widthsById;
  }, [widthsById]);

  const dispatch = useMemo<BoxedExpressionEditorDispatchContextType>(
    () => ({
      setExpression: onExpressionChange,
      setWidthsById: (mutation) => {
        const newWidthsById = new Map(widthsByIdRef.current);
        widthsByIdRef.current = newWidthsById;
        mutation({ newMap: newWidthsById });
        onWidthsChange(newWidthsById);
      },
    }),
    [onExpressionChange, onWidthsChange]
  );

  return (
    <BoxedExpressionEditorContext.Provider
      value={{
        //plumbing
        beeGwtService, // Move to a separate context?
        editorRef,
        scrollableParentRef,

        // props
        expressionHolderId,
        dataTypes,
        pmmlDocuments,

        //state // FIXME: Move to a separate context (https://github.com/apache/incubator-kie-issues/issues/168)
        currentlyOpenContextMenu,
        setCurrentlyOpenContextMenu,
        onRequestFeelVariables,
        widthsById,
        hideDmn14BoxedExpressions,
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

export type OnSetExpression = (args: {
  getNewExpression: (prev: BoxedExpression | undefined) => BoxedExpression | undefined;
}) => void;

export function NestedExpressionDispatchContextProvider({
  onSetExpression,
  children,
}: React.PropsWithChildren<{
  onSetExpression: OnSetExpression;
}>) {
  const { setWidthsById } = useBoxedExpressionEditorDispatch();
  const nestedExpressionDispatch = useMemo<BoxedExpressionEditorDispatchContextType>(() => {
    return {
      setExpression: (newExpressionAction: React.SetStateAction<BoxedExpression>) => {
        function getNewExpression(prev: BoxedExpression) {
          return typeof newExpressionAction === "function" ? newExpressionAction(prev) : newExpressionAction;
        }

        onSetExpression({ getNewExpression });
      },
      setWidthsById,
    };
  }, [onSetExpression, setWidthsById]);

  return (
    <BoxedExpressionEditorDispatchContext.Provider value={nestedExpressionDispatch}>
      {children}
    </BoxedExpressionEditorDispatchContext.Provider>
  );
}
