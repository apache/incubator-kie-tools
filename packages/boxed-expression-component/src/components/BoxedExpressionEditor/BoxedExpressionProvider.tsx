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
import { ExpressionProps, PMMLParams } from "../../api";
import { CellSelectionBox } from "../SelectionBox";

export interface BoxedExpressionProviderProps {
  /** All expression properties used to define it */
  expressionDefinition: ExpressionProps;
  /** PMML parameters */
  pmmlParams?: PMMLParams;
  /** Flag that changes how the resize works when being used by the DMN Runner **/
  isRunnerTable: boolean;
  /** Children component **/
  children: React.ReactNode;
}

export function BoxedExpressionProvider(props: BoxedExpressionProviderProps) {
  const [currentlyOpenedHandlerCallback, setCurrentlyOpenedHandlerCallback] = useState(() => _.identity);
  const boxedExpressionEditorRef = useRef<HTMLDivElement>(null);
  const [supervisorHash, setSupervisorHash] = useState(hashfy(props.expressionDefinition));

  useEffect(() => {
    setSupervisorHash(hashfy(props.expressionDefinition));
  }, [props.expressionDefinition]);

  return (
    <BoxedExpressionGlobalContext.Provider
      value={{
        pmmlParams: props.pmmlParams,
        supervisorHash,
        setSupervisorHash,
        boxedExpressionEditorRef,
        currentlyOpenedHandlerCallback,
        setCurrentlyOpenedHandlerCallback,
      }}
    >
      <ResizerSupervisor isRunnerTable={props.isRunnerTable}>
        <div className="boxed-expression-editor" ref={boxedExpressionEditorRef}>
          {props.children}
        </div>
      </ResizerSupervisor>
      <CellSelectionBox />
    </BoxedExpressionGlobalContext.Provider>
  );
}
