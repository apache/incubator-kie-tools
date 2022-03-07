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
import { useEffect, useRef, useState } from "react";
import "./BoxedExpressionProvider.css";
import { hashfy, ResizerSupervisor } from "../Resizer";
import { BoxedExpressionGlobalContext } from "../../context";
import * as _ from "lodash";
import { CellSelectionBox } from "../SelectionBox";
import { BoxedExpressionEditorProps } from "./BoxedExpressionEditor";

export interface BoxedExpressionProviderProps extends BoxedExpressionEditorProps {
  /** Flag that changes how the resize works when being used by the DMN Runner **/
  isRunnerTable: boolean;
  /** Children component **/
  children: React.ReactNode;
}

export function BoxedExpressionProvider(props: BoxedExpressionProviderProps) {
  const [currentlyOpenedHandlerCallback, setCurrentlyOpenedHandlerCallback] = useState(() => _.identity);
  const [supervisorHash, setSupervisorHash] = useState(hashfy(props.expressionDefinition));
  const [isContextMenuOpen, setIsContextMenuOpen] = useState(false);
  const editorRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    setSupervisorHash(hashfy(props.expressionDefinition));
  }, [props.expressionDefinition]);

  return (
    <BoxedExpressionGlobalContext.Provider
      value={{
        boxedExpressionEditorGWTService: props.boxedExpressionEditorGWTService,
        decisionNodeId: props.decisionNodeId,
        dataTypes: props.dataTypes,
        pmmlParams: props.pmmlParams,
        supervisorHash,
        setSupervisorHash,
        isContextMenuOpen,
        setIsContextMenuOpen,
        editorRef,
        currentlyOpenedHandlerCallback,
        setCurrentlyOpenedHandlerCallback,
      }}
    >
      <ResizerSupervisor isRunnerTable={props.isRunnerTable}>
        <div className="boxed-expression-provider" ref={editorRef}>
          {props.children}
        </div>
      </ResizerSupervisor>
      {props.isRunnerTable === false && <CellSelectionBox />}
    </BoxedExpressionGlobalContext.Provider>
  );
}
