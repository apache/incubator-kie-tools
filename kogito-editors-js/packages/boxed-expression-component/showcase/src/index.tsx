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
import { useState } from "react";
import * as ReactDOM from "react-dom";
import "./index.css";
// noinspection ES6PreferShortImport
import {
  BoxedExpressionEditor,
  ContextProps,
  DataType,
  DecisionTableProps,
  ExpressionContainerProps,
  ExpressionProps,
  FunctionProps,
  InvocationProps,
  ListProps,
  LiteralExpressionProps,
  RelationProps,
} from "./lib";

export const App: React.FunctionComponent = () => {
  //This definition comes directly from the decision node
  const selectedExpression: ExpressionProps = {
    name: "Expression Name",
    dataType: DataType.Undefined,
  };

  const pmmlParams = [
    {
      document: "mining pmml",
      modelsFromDocument: [
        {
          model: "MiningModelSum",
          parametersFromModel: [
            { name: "input1", dataType: DataType.Any },
            { name: "input2", dataType: DataType.Any },
            { name: "input3", dataType: DataType.Any },
          ],
        },
      ],
    },
    {
      document: "regression pmml",
      modelsFromDocument: [
        {
          model: "RegressionLinear",
          parametersFromModel: [
            { name: "i1", dataType: DataType.Number },
            { name: "i2", dataType: DataType.Number },
          ],
        },
      ],
    },
  ];

  const [updatedExpression, setUpdatedExpression] = useState(selectedExpression);

  const expressionDefinition: ExpressionContainerProps = { selectedExpression };

  //Defining global function that will be available in the Window namespace and used by the BoxedExpressionEditor component
  window.beeApi = {
    resetExpressionDefinition: (definition: ExpressionProps) => setUpdatedExpression(definition),
    broadcastLiteralExpressionDefinition: (definition: LiteralExpressionProps) => setUpdatedExpression(definition),
    broadcastRelationExpressionDefinition: (definition: RelationProps) => setUpdatedExpression(definition),
    broadcastContextExpressionDefinition: (definition: ContextProps) => setUpdatedExpression(definition),
    broadcastListExpressionDefinition: (definition: ListProps) => setUpdatedExpression(definition),
    broadcastInvocationExpressionDefinition: (definition: InvocationProps) => setUpdatedExpression(definition),
    broadcastFunctionExpressionDefinition: (definition: FunctionProps) => setUpdatedExpression(definition),
    broadcastDecisionTableExpressionDefinition: (definition: DecisionTableProps) => setUpdatedExpression(definition),
  };

  return (
    <div className="showcase">
      <div className="boxed-expression">
        <BoxedExpressionEditor expressionDefinition={expressionDefinition} pmmlParams={pmmlParams} />
      </div>
      <div className="updated-json">
        <pre>{JSON.stringify(updatedExpression, null, 2)}</pre>
      </div>
    </div>
  );
};

ReactDOM.render(<App />, document.getElementById("root"));
