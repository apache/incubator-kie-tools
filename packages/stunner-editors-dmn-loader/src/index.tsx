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

import {
  BoxedExpressionEditor,
  BoxedExpressionEditorProps,
  ContextProps,
  DecisionTableProps,
  ExpressionProps,
  FunctionProps,
  InvocationProps,
  ListProps,
  LiteralExpressionProps,
  LogicType,
  PMMLParams,
  RelationProps,
} from "@kogito-tooling/boxed-expression-component";
import * as React from "react";
import { useEffect, useState } from "react";
import * as ReactDOM from "react-dom";

const BoxedExpressionWrapper: React.FunctionComponent<BoxedExpressionEditorProps> = ({
  decisionNodeId,
  expressionDefinition,
  clearSupportedOnRootExpression,
  pmmlParams,
}: BoxedExpressionEditorProps) => {
  const [updatedDefinition, setExpressionDefinition] = useState<ExpressionProps>(expressionDefinition);

  useEffect(() => {
    setExpressionDefinition({ logicType: LogicType.Undefined });
    setTimeout(() => {
      setExpressionDefinition(expressionDefinition);
    }, 0);
  }, [expressionDefinition]);

  //The wrapper defines these function in order to keep expression definition state updated,
  //And to propagate such definition to DMN Editor (GWT world), by calling beeApiWrapper APIs
  window.beeApi = {
    notifyUserAction(): void {
      window.beeApiWrapper?.notifyUserAction();
    },
    resetExpressionDefinition: (definition: ExpressionProps) => {
      setExpressionDefinition(definition);
      window.beeApiWrapper?.resetExpressionDefinition?.(definition);
    },
    broadcastLiteralExpressionDefinition: (definition: LiteralExpressionProps) => {
      setExpressionDefinition(definition);
      window.beeApiWrapper?.broadcastLiteralExpressionDefinition?.(definition);
    },
    broadcastRelationExpressionDefinition: (definition: RelationProps) => {
      setExpressionDefinition(definition);
      window.beeApiWrapper?.broadcastRelationExpressionDefinition?.(definition);
    },
    broadcastContextExpressionDefinition: (definition: ContextProps) => {
      setExpressionDefinition(definition);
      window.beeApiWrapper?.broadcastContextExpressionDefinition?.(definition);
    },
    broadcastListExpressionDefinition: (definition: ListProps) => {
      setExpressionDefinition(definition);
      window.beeApiWrapper?.broadcastListExpressionDefinition?.(definition);
    },
    broadcastInvocationExpressionDefinition: (definition: InvocationProps) => {
      setExpressionDefinition(definition);
      window.beeApiWrapper?.broadcastInvocationExpressionDefinition?.(definition);
    },
    broadcastFunctionExpressionDefinition: (definition: FunctionProps) => {
      setExpressionDefinition(definition);
      window.beeApiWrapper?.broadcastFunctionExpressionDefinition?.(definition);
    },
    broadcastDecisionTableExpressionDefinition: (definition: DecisionTableProps) => {
      setExpressionDefinition(definition);
      window.beeApiWrapper?.broadcastDecisionTableExpressionDefinition?.(definition);
    },
  };

  return (
    <BoxedExpressionEditor
      decisionNodeId={decisionNodeId}
      expressionDefinition={updatedDefinition}
      clearSupportedOnRootExpression={clearSupportedOnRootExpression}
      pmmlParams={pmmlParams}
    />
  );
};

const renderBoxedExpressionEditor = (
  selector: string,
  decisionNodeId: string,
  expressionDefinition: ExpressionProps,
  clearSupportedOnRootExpression: boolean,
  pmmlParams: PMMLParams
) => {
  ReactDOM.render(
    <BoxedExpressionWrapper
      decisionNodeId={decisionNodeId}
      expressionDefinition={expressionDefinition}
      clearSupportedOnRootExpression={clearSupportedOnRootExpression}
      pmmlParams={pmmlParams}
    />,
    document.querySelector(selector)
  );
};

export { renderBoxedExpressionEditor };
