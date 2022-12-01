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
  executeIfExpressionDefinitionChanged,
  ExpressionDefinitionLogicType,
} from "@kie-tools/boxed-expression-component/dist/api";

describe("BoxedExpressionEditor tests", () => {
  describe("executeIfExpressionDefinitionChanged function", () => {
    test("when prevDef and updatedDef are not equal, functionToExecute gets executed", () => {
      const mockedFunctionToExecute = jest.fn();

      executeIfExpressionDefinitionChanged(
        { name: "1", logicType: ExpressionDefinitionLogicType.Undefined },
        { name: "2", logicType: ExpressionDefinitionLogicType.Undefined },
        mockedFunctionToExecute,
        ["name"]
      );

      expect(mockedFunctionToExecute).toHaveBeenCalledTimes(1);
    });

    test("when prevDef and updatedDef are equal, functionToExecute is not executed", () => {
      const mockedFunctionToExecute = jest.fn();

      executeIfExpressionDefinitionChanged(
        { name: "1", logicType: ExpressionDefinitionLogicType.Undefined },
        { name: "1", logicType: ExpressionDefinitionLogicType.Undefined },
        mockedFunctionToExecute,
        ["name"]
      );

      expect(mockedFunctionToExecute).toHaveBeenCalledTimes(0);
    });

    test("when prevDef and updatedDef are not equal, but propertiesToCheck contains a field for which they are equal, functionToExecute is not executed", () => {
      const mockedFunctionToExecute = jest.fn();

      executeIfExpressionDefinitionChanged(
        { name: "1", logicType: ExpressionDefinitionLogicType.Undefined },
        { name: "1", logicType: ExpressionDefinitionLogicType.LiteralExpression },
        mockedFunctionToExecute,
        ["name"]
      );

      expect(mockedFunctionToExecute).toHaveBeenCalledTimes(0);
    });

    test("when prevDef and updatedDef are not equal and propertiesToCheck is not passed, functionToExecute gets executed", () => {
      const mockedFunctionToExecute = jest.fn();

      executeIfExpressionDefinitionChanged(
        { name: "1", logicType: ExpressionDefinitionLogicType.Undefined },
        { name: "2", logicType: ExpressionDefinitionLogicType.Undefined },
        mockedFunctionToExecute
      );

      expect(mockedFunctionToExecute).toHaveBeenCalledTimes(1);
    });
  });
});
