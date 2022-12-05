/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
import { useContext, useMemo } from "react";
import { BeeGwtService, DmnDataType, ExpressionDefinition, PmmlParam } from "../../api";
import { useEffect, useRef, useState } from "react";
import "./BoxedExpressionEditorContextProvider.css";
import { hashfy, ResizerSupervisor } from "../Resizer";
import * as _ from "lodash";
import { CellSelectionBox } from "../SelectionBox";
import { BoxedExpressionEditorProps } from "./BoxedExpressionEditor";

export interface BoxedExpressionEditorContextType {
  // Plumbing
  beeGwtService?: BeeGwtService;
  editorRef: React.RefObject<HTMLDivElement>;

  // Props
  decisionNodeId: string;
  pmmlParams?: PmmlParam[];
  dataTypes: DmnDataType[];

  // State
  supervisorHash: string;
  setSupervisorHash: (hash: string) => void;
  isContextMenuOpen: boolean;
  setContextMenuOpen: React.Dispatch<React.SetStateAction<boolean>>;
  currentlyOpenedHandlerCallback: React.Dispatch<React.SetStateAction<boolean>>;
  setCurrentlyOpenedHandlerCallback: React.Dispatch<
    React.SetStateAction<React.Dispatch<React.SetStateAction<boolean>>>
  >;
}

export interface BoxedExpressionEditorDispatchContextType {
  setExpression: React.Dispatch<React.SetStateAction<ExpressionDefinition>>;
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

export interface BoxedExpressionEditorContextProviderProps extends React.PropsWithChildren<BoxedExpressionEditorProps> {
  /** Flag that changes how the resize works when being used by the DMN Runner **/
  isRunnerTable: boolean;
}

export function BoxedExpressionEditorContextProvider(props: BoxedExpressionEditorContextProviderProps) {
  const [currentlyOpenedHandlerCallback, setCurrentlyOpenedHandlerCallback] = useState(() => _.identity);
  const [supervisorHash, setSupervisorHash] = useState(hashfy(props.expressionDefinition));
  const [isContextMenuOpen, setContextMenuOpen] = useState(false);
  const editorRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    setSupervisorHash(hashfy(props.expressionDefinition));
  }, [props.expressionDefinition]);

  const dispatch = useMemo(() => {
    return {
      setExpression: props.setExpressionDefinition,
    };
  }, [props.setExpressionDefinition]);

  return (
    <BoxedExpressionEditorContext.Provider
      value={{
        //plumbing
        beeGwtService: props.beeGwtService,
        editorRef,

        // props
        decisionNodeId: props.decisionNodeId,
        dataTypes: props.dataTypes,
        pmmlParams: props.pmmlParams,

        //state
        supervisorHash,
        setSupervisorHash,
        isContextMenuOpen,
        setContextMenuOpen,
        currentlyOpenedHandlerCallback,
        setCurrentlyOpenedHandlerCallback,
      }}
    >
      <BoxedExpressionEditorDispatchContext.Provider value={dispatch}>
        <ResizerSupervisor isRunnerTable={props.isRunnerTable}>
          <div className="boxed-expression-provider" ref={editorRef}>
            {props.children}
          </div>
        </ResizerSupervisor>
        {props.isRunnerTable === false && <CellSelectionBox />}
      </BoxedExpressionEditorDispatchContext.Provider>
    </BoxedExpressionEditorContext.Provider>
  );
}
