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
import { useEffect, useState } from "react";
import {
  BeeGwtService,
  DmnBuiltInDataType,
  ExpressionDefinitionLogicType,
  ExpressionDefinition,
  generateUuid,
} from "../../src/api";
import { getDefaultExpressionDefinitionByLogicType } from "./defaultExpression";
import type { Meta, StoryObj } from "@storybook/react";
import { BoxedExpressionEditorWrapper } from "../boxedExpressionWrapper";
import { BoxedExpressionEditorProps } from "../../src/expressions";

/**
 * Constants copied from tests to fix debugger
 */
const dataTypes = [
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

const pmmlParams = [
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
  id: generateUuid(),
  name: "Expression Name",
  logicType: ExpressionDefinitionLogicType.Undefined,
  dataType: DmnBuiltInDataType.Undefined,
};

//Defining global function that will be available in the Window namespace and used by the BoxedExpressionEditor component
const beeGwtService: BeeGwtService = {
  getDefaultExpressionDefinition(logicType: string, dataType: string): ExpressionDefinition {
    return getDefaultExpressionDefinitionByLogicType(
      logicType as ExpressionDefinitionLogicType,
      { dataType: dataType } as ExpressionDefinition,
      0
    );
  },
  openDataTypePage(): void {},
  selectObject(): void {},
};

function App(args: BoxedExpressionEditorProps) {
  const [version, setVersion] = useState(-1);
  const [expressionDefinition, setExpressionDefinition] = useState<ExpressionDefinition>(INITIAL_EXPRESSION);

  useEffect(() => {
    setVersion((prev) => prev + 1);
  }, [expressionDefinition]);

  return (
    <div className="dev-webapp">
      <h2> DEVE WEB APP</h2>
      <h3 style={{ position: "absolute", right: 0 }}>updates count: {version}&nbsp;&nbsp;</h3>
      <div className="boxed-expression">
        {BoxedExpressionEditorWrapper({
          decisionNodeId: "_00000000-0000-0000-0000-000000000000",
          dataTypes: args.dataTypes,
          beeGwtService: args.beeGwtService,
          pmmlParams: args.pmmlParams,
          expressionDefinition: expressionDefinition,
          setExpressionDefinition: setExpressionDefinition,
        })}
      </div>
    </div>
  );
}

const meta: Meta<typeof App> = {
  title: "_dev/Web App",
  component: App,
};

export default meta;
type Story = StoryObj<typeof App>;

export const WebApp: Story = {
  render: (args) => App(args),
  args: {
    decisionNodeId: "_00000000-0000-0000-0000-000000000000",
    expressionDefinition: {
      id: generateUuid(),
      name: "Expression Name",
      dataType: DmnBuiltInDataType.Undefined,
      logicType: ExpressionDefinitionLogicType.Undefined,
    },
    dataTypes: dataTypes,
    beeGwtService: beeGwtService,
    pmmlParams: pmmlParams,
  },
};
