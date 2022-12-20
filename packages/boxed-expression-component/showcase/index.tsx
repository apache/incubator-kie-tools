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
import { useCallback, useEffect, useState } from "react";
import * as ReactDOM from "react-dom";
import "./index.css";
// noinspection ES6PreferShortImport
import { BeeGwtService, DmnBuiltInDataType, ExpressionDefinitionLogicType, ExpressionDefinition } from "../src/api";
import { BoxedExpressionEditor } from "../src/expressions";
import { Button, Modal } from "@patternfly/react-core";
import { PenIcon } from "@patternfly/react-icons/dist/js/icons/pen-icon";
import "../src/expressions/BoxedExpressionEditor/base-no-reset-wrapped.css";
import ReactJson from "react-json-view";

/**
 * Constants copied from tests to fix debugger
 */
export const dataTypes = [
  { typeRef: "Undefined", name: "<Undefined>", isCustom: false },
  { typeRef: "Any", name: "Any", isCustom: false },
  { typeRef: "Boolean", name: "boolean", isCustom: false },
  { typeRef: "Context", name: "context", isCustom: false },
  { typeRef: "Date", name: "date", isCustom: false },
  { typeRef: "DateTime", name: "date and time", isCustom: false },
  { typeRef: "DateTimeDuration", name: "days and time duration", isCustom: false },
  { typeRef: "Number", name: "number", isCustom: false },
  { typeRef: "String", name: "string", isCustom: false },
  { typeRef: "Time", name: "time", isCustom: false },
  { typeRef: "YearsMonthsDuration", name: "years and months duration", isCustom: false },
  { typeRef: "tPerson", name: "tPerson", isCustom: true },
];

export const pmmlParams = [
  {
    document: "document",
    modelsFromDocument: [
      { model: "model", parametersFromModel: [{ id: "p1", name: "p-1", dataType: DmnBuiltInDataType.Number }] },
    ],
  },
  {
    document: "mining pmml",
    modelsFromDocument: [
      {
        model: "MiningModelSum",
        parametersFromModel: [
          { id: "i1", name: "input1", dataType: DmnBuiltInDataType.Any },
          { id: "i2", name: "input2", dataType: DmnBuiltInDataType.Any },
          { id: "i3", name: "input3", dataType: DmnBuiltInDataType.Any },
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
          { id: "i1", name: "i1", dataType: DmnBuiltInDataType.Number },
          { id: "i2", name: "i2", dataType: DmnBuiltInDataType.Number },
        ],
      },
    ],
  },
];

const INITIAL_EXPRESSION: ExpressionDefinition = {
  name: "Expression Name",
  logicType: ExpressionDefinitionLogicType.Undefined,
};

//Defining global function that will be available in the Window namespace and used by the BoxedExpressionEditor component
const beeGwtService: BeeGwtService = {
  resetExpressionDefinition: (definition) => {},
  broadcastLiteralExpressionDefinition: (definition) => {},
  broadcastRelationExpressionDefinition: (definition) => {},
  broadcastContextExpressionDefinition: (definition) => {},
  broadcastListExpressionDefinition: (definition) => {},
  broadcastInvocationExpressionDefinition: (definition) => {},
  broadcastFunctionExpressionDefinition: (definition) => {},
  broadcastDecisionTableExpressionDefinition: (definition) => {},
  notifyUserAction(): void {},
  openManageDataType(): void {},
  onLogicTypeSelect(): void {},
  selectObject(): void {},
};

export const App: React.FunctionComponent = () => {
  const [expression, setExpression] = useState<ExpressionDefinition>(INITIAL_EXPRESSION);
  const [expressionString, setExpressionString] = useState(JSON.stringify(INITIAL_EXPRESSION));
  const [isModalOpen, setIsModalOpen] = useState(false);

  const onExpressionStringChange = useCallback((e: React.ChangeEvent<HTMLTextAreaElement>) => {
    setExpressionString(e.target.value);
  }, []);

  const toggleModal = useCallback(() => {
    setIsModalOpen((prev) => !prev);
  }, []);

  const setExpressionFromString = useCallback(() => {
    try {
      const parsedTypedExpression = JSON.parse(expressionString);
      setExpression(parsedTypedExpression);
      setIsModalOpen(false);
    } catch (e) {
      console.error(e);
    }
  }, [expressionString]);

  useEffect(() => {
    setExpressionString(JSON.stringify(expression));
  }, [expression]);

  return (
    <div className="showcase">
      <div className="boxed-expression">
        <BoxedExpressionEditor
          beeGwtService={beeGwtService}
          decisionNodeId="_00000000-0000-0000-0000-000000000000"
          expressionDefinition={expression}
          setExpressionDefinition={setExpression}
          dataTypes={dataTypes}
          pmmlParams={pmmlParams}
        />
      </div>

      <div className="updated-json">
        <div className="buttons">
          <Button
            variant="secondary"
            icon={<PenIcon />}
            iconPosition="left"
            onClick={toggleModal}
            ouiaId="edit-expression-json"
          />
        </div>

        <pre>
          <ReactJson src={expression} name={false} enableClipboard />
        </pre>
      </div>

      <Modal
        title="Manually edit Expression Definition"
        className="expression-definition-editor-modal"
        isOpen={isModalOpen}
        onClose={toggleModal}
        description="This modal is supposed to provide a manual edit option for the expression definition. If «Confirm» action does nothing, probably there is an issue with JSON definition parsing: look at browser's console."
        actions={[
          <Button key="confirm" variant="primary" onClick={setExpressionFromString} ouiaId="confirm-expression-json">
            Confirm
          </Button>,
          <Button key="cancel" variant="link" onClick={toggleModal}>
            Cancel
          </Button>,
        ]}
      >
        <textarea
          className="typed-expression"
          value={expressionString}
          onChange={onExpressionStringChange}
          data-ouia-component-id="typed-expression-json"
        />
      </Modal>
    </div>
  );
};

ReactDOM.render(<App />, document.getElementById("root"));
