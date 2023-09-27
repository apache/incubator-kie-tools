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
import { ExpressionDefinition } from "../../api";
import { ResizingWidthsContextProvider } from "../../resizing/ResizingWidthsContext";
import { ExpressionContainer } from "./ExpressionContainer";
import "./ExpressionDefinitionRoot.css";

export interface ExpressionDefinitionRootProps {
  decisionNodeId: string;
  expression: ExpressionDefinition;
  isResetSupported: boolean | undefined;
}

export function ExpressionDefinitionRoot({
  decisionNodeId,
  expression,
  isResetSupported = true,
}: ExpressionDefinitionRootProps) {
  return (
    <ResizingWidthsContextProvider>
      <div className={`expression-container ${decisionNodeId}`}>
        <div className="expression-name-and-logic-type">
          <span className="expression-title">{expression.name}</span>
          &nbsp;
          <span className="expression-type">({expression.logicType})</span>
        </div>

        <ExpressionContainer
          expression={expression}
          isResetSupported={isResetSupported}
          isNested={false}
          rowIndex={0}
          columnIndex={0}
          parentElementId={decisionNodeId}
        />
      </div>
    </ResizingWidthsContextProvider>
  );
}
