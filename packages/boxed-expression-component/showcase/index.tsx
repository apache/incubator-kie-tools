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
import { useCallback, useState } from "react";
import * as ReactDOM from "react-dom";
import "./index.css";
// noinspection ES6PreferShortImport
import {
  BoxedExpressionEditor,
  ContextProps,
  DataType,
  DecisionTableProps,
  ExpressionProps,
  FunctionProps,
  InvocationProps,
  ListProps,
  LiteralExpressionProps,
  LogicType,
  RelationProps,
} from "../src";
import { Button, Modal } from "@patternfly/react-core";
import { CopyIcon, PenIcon } from "@patternfly/react-icons";
import "../src/components/BoxedExpressionEditor/base-no-reset-wrapped.css";
import ReactJson from "react-json-view";

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
            { id: "i1", name: "input1", dataType: DataType.Any },
            { id: "i2", name: "input2", dataType: DataType.Any },
            { id: "i3", name: "input3", dataType: DataType.Any },
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
            { id: "i1", name: "i1", dataType: DataType.Number },
            { id: "i2", name: "i2", dataType: DataType.Number },
          ],
        },
      ],
    },
  ];

  const [expressionDefinition, setExpressionDefinition] = useState(selectedExpression);

  const [typedExpressionDefinition, setTypedExpressionDefinition] = useState(JSON.stringify(selectedExpression));

  const [isModalOpen, setIsModalOpen] = useState(false);

  //Defining global function that will be available in the Window namespace and used by the BoxedExpressionEditor component
  window.beeApi = {
    resetExpressionDefinition: (definition: ExpressionProps) => setExpressionDefinition(definition),
    broadcastLiteralExpressionDefinition: (definition: LiteralExpressionProps) => setExpressionDefinition(definition),
    broadcastRelationExpressionDefinition: (definition: RelationProps) => setExpressionDefinition(definition),
    broadcastContextExpressionDefinition: (definition: ContextProps) => setExpressionDefinition(definition),
    broadcastListExpressionDefinition: (definition: ListProps) => setExpressionDefinition(definition),
    broadcastInvocationExpressionDefinition: (definition: InvocationProps) => setExpressionDefinition(definition),
    broadcastFunctionExpressionDefinition: (definition: FunctionProps) => setExpressionDefinition(definition),
    broadcastDecisionTableExpressionDefinition: (definition: DecisionTableProps) => setExpressionDefinition(definition),
    notifyUserAction(): void {},
  };

  const copyToClipboard = useCallback(
    () => navigator.clipboard.writeText(JSON.stringify(expressionDefinition)),
    [expressionDefinition]
  );

  const onTypedExpressionChange = useCallback((e) => {
    setTypedExpressionDefinition(e.target.value);
  }, []);

  const handleModalToggle = useCallback(() => {
    setIsModalOpen((isModalOpen) => {
      const goingToOpenTheModal = !isModalOpen;
      if (goingToOpenTheModal) {
        setTypedExpressionDefinition(JSON.stringify(expressionDefinition));
      }
      return goingToOpenTheModal;
    });
  }, [expressionDefinition]);

  const updateExpressionDefinition = useCallback(() => {
    try {
      const parsedTypedExpression = JSON.parse(typedExpressionDefinition);
      setExpressionDefinition({ logicType: LogicType.Undefined });
      setTimeout(() => {
        setExpressionDefinition(parsedTypedExpression);
      }, 0);
      setIsModalOpen(false);
    } catch (e) {
      console.error(e);
    }
  }, [typedExpressionDefinition]);

  return (
    <div className="showcase">
      <div className="boxed-expression">
        <BoxedExpressionEditor
          decisionNodeId="_00000000-0000-0000-0000-000000000000"
          expressionDefinition={expressionDefinition}
          pmmlParams={pmmlParams}
        />
      </div>

      <div className="updated-json">
        <div className="buttons">
          <Button
            variant="secondary"
            icon={<CopyIcon />}
            iconPosition="left"
            onClick={copyToClipboard}
            ouiaId="copy-expression-json"
          />
          <Button
            variant="secondary"
            icon={<PenIcon />}
            iconPosition="left"
            onClick={handleModalToggle}
            ouiaId="edit-expression-json"
          />
        </div>

        <pre>
          <ReactJson src={expressionDefinition} name={false} />
        </pre>
      </div>

      <Modal
        title="Manually edit Expression Definition"
        className="expression-definition-editor-modal"
        isOpen={isModalOpen}
        onClose={handleModalToggle}
        description="This modal is supposed to provide a manual edit option for the expression definition. If «Confirm» action does nothing, probably there is an issue with JSON definition parsing: look at browser's console."
        actions={[
          <Button key="confirm" variant="primary" onClick={updateExpressionDefinition} ouiaId="confirm-expression-json">
            Confirm
          </Button>,
          <Button key="cancel" variant="link" onClick={handleModalToggle}>
            Cancel
          </Button>,
        ]}
      >
        <textarea
          className="typed-expression"
          value={typedExpressionDefinition}
          onChange={onTypedExpressionChange}
          data-ouia-component-id="typed-expression-json"
        />
      </Modal>
    </div>
  );
};

ReactDOM.render(<App />, document.getElementById("root"));
