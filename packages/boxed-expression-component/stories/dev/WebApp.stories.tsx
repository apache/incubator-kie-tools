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
import { useEffect, useState, useCallback } from "react";
import { BeeGwtService, DmnBuiltInDataType, ExpressionDefinition } from "../../src/api";
import { getDefaultExpressionDefinitionByLogicType } from "./defaultExpression";
import type { Meta, StoryObj } from "@storybook/react";
import { BoxedExpressionEditorWrapper } from "../boxedExpressionStoriesWrapper";
import { BoxedExpressionEditorProps } from "../../src/expressions";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Button, Flex, FlexItem, Tooltip } from "@patternfly/react-core/dist/js";
import { canDriveExpressionDefinition, canDriveWidthsById } from "../useCases/CanDrive/CanDrive.stories";
import {
  findEmployeesByKnowledgeExpression,
  findEmployeesByKnowledgeWidthsById,
} from "../useCases/FindEmployees/FindEmployees.stories";
import {
  postBureauAffordabilityExpression,
  postBureauAffordabilityWidthsById,
} from "../useCases/LoanOriginations/RoutingDecisionService/PostBureauAffordability/PostBureauAffordability.stories";

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

const pmmlDocuments = [
  {
    document: "document",
    modelsFromDocument: [
      {
        model: "model",
        parametersFromModel: [{ "@_id": "p1", "@_name": "p-1", "@_typeRef": DmnBuiltInDataType.Number }],
      },
    ],
  },
  {
    document: "mining pmml",
    modelsFromDocument: [
      {
        model: "MiningModelSum",
        parametersFromModel: [
          { "@_id": "i1", "@_name": "input1", "@_typeRef": DmnBuiltInDataType.Any },
          { "@_id": "i2", "@_name": "input2", "@_typeRef": DmnBuiltInDataType.Any },
          { "@_id": "i3", "@_name": "input3", "@_typeRef": DmnBuiltInDataType.Any },
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
          { "@_id": "i1", "@_name": "i1", "@_typeRef": DmnBuiltInDataType.Number },
          { "@_id": "i2", "@_name": "i2", "@_typeRef": DmnBuiltInDataType.Number },
        ],
      },
    ],
  },
];

const INITIAL_EXPRESSION: ExpressionDefinition | undefined = undefined;
const INITIAL_WIDTHS_BY_ID: Map<string, number[]> | undefined = undefined;

//Defining global function that will be available in the Window namespace and used by the BoxedExpressionEditor component
const beeGwtService: BeeGwtService = {
  getDefaultExpressionDefinition(logicType, dataType) {
    return {
      expression: getDefaultExpressionDefinitionByLogicType(logicType, dataType, 0),
      widthsById: new Map(),
    };
  },
  openDataTypePage(): void {},
  selectObject(): void {},
};

function App(args: BoxedExpressionEditorProps) {
  const [version, setVersion] = useState(-1);
  const [expressionDefinition, setExpressionDefinition] = useState<ExpressionDefinition | undefined>(
    INITIAL_EXPRESSION
  );
  const [widthsById, setWidthsById] = useState<Map<string, number[]> | undefined>(INITIAL_WIDTHS_BY_ID);

  useEffect(() => {
    setVersion((prev) => prev + 1);
  }, [expressionDefinition]);

  const setSample = useCallback((sample?: ExpressionDefinition, widthsById?: Map<string, number[]>) => {
    setExpressionDefinition(sample);
    setWidthsById(widthsById);
  }, []);

  return (
    <div>
      <Flex direction={{ default: "column" }}>
        <FlexItem>
          <Flex style={{ width: "96vw" }}>
            <FlexItem>
              <Button onClick={() => setSample(undefined, undefined)}>Empty</Button>
            </FlexItem>
            <FlexItem>
              <Button onClick={() => setSample(canDriveExpressionDefinition, canDriveWidthsById)}>Can Drive?</Button>
            </FlexItem>
            <FlexItem>
              <Button onClick={() => setSample(findEmployeesByKnowledgeExpression, findEmployeesByKnowledgeWidthsById)}>
                Find Employees by Knowledge
              </Button>
            </FlexItem>
            <FlexItem>
              <Button onClick={() => setSample(postBureauAffordabilityExpression, postBureauAffordabilityWidthsById)}>
                Affordability
              </Button>
            </FlexItem>
            <FlexItem align={{ default: "alignRight" }}>
              <Tooltip content={"This number updates everytime the expressionDefinition object is updated"}>
                <Title headingLevel="h2">Updates count: {version}</Title>
              </Tooltip>
            </FlexItem>
          </Flex>
        </FlexItem>
        <FlexItem>
          <div>
            {BoxedExpressionEditorWrapper({
              expressionHolderId: "_00000000-0000-0000-0000-000000000000",
              dataTypes: args.dataTypes,
              beeGwtService: args.beeGwtService,
              pmmlDocuments: args.pmmlDocuments,
              expression: expressionDefinition,
              onExpressionChange: setExpressionDefinition,
              widthsById: widthsById,
            })}
          </div>
        </FlexItem>
      </Flex>
    </div>
  );
}

const meta: Meta<typeof App> = {
  title: "Dev/Web App",
  component: App,
};

export default meta;
type Story = StoryObj<typeof App>;

export const WebApp: Story = {
  render: (args) => App(args),
  args: {
    expressionHolderId: "_00000000-0000-0000-0000-000000000000",
    expression: undefined,
    dataTypes: dataTypes,
    beeGwtService: beeGwtService,
    pmmlDocuments: pmmlDocuments,
    widthsById: undefined,
  },
};
