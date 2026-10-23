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

import {
  BPMN20_MODEL_NAMESPACE,
  DataMapping,
  setInputDataMapping,
  setOutputDataMapping,
} from "../../src/mutations/_dataMapping";

const itemDefinitionIdByDataTypes = new Map<string, string>([["Object", "_itemDefinition"]]);

const expressionMapping: DataMapping = {
  name: "myData",
  dtype: "Object",
  isExpression: true,
  value: "some-expression",
};

const variableMapping: DataMapping = {
  name: "myData",
  dtype: "Object",
  isExpression: false,
  variableRef: "myVariable",
};

const definitionsWithBpmn2Prefix = {
  "@_id": "def1",
  "@_xmlns:bpmn2": BPMN20_MODEL_NAMESPACE,
} as any;

const definitionsWithDefaultNamespace = {
  "@_id": "def1",
  "@_xmlns": BPMN20_MODEL_NAMESPACE,
} as any;

describe("setInputDataMapping", () => {
  function run(dataMapping: DataMapping, definitions?: any) {
    const element: any = {};
    setInputDataMapping(itemDefinitionIdByDataTypes, [dataMapping], element, element, definitions);
    return element;
  }

  it("uses the 'bpmn2:' prefix in @_xsi:type when BPMN model namespace is bound to bpmn2", () => {
    const element = run(expressionMapping, definitionsWithBpmn2Prefix);

    expect(element.dataInputAssociation).toHaveLength(1);
    expect(element.dataInputAssociation[0].assignment[0].from["@_xsi:type"]).toBe("bpmn2:tFormalExpression");
    expect(element.dataInputAssociation[0].assignment[0].from.__$$text).toBe("some-expression");
  });

  it("uses no prefix in @_xsi:type when BPMN model namespace is the default xmlns", () => {
    const element = run(expressionMapping, definitionsWithDefaultNamespace);

    expect(element.dataInputAssociation[0].assignment[0].from["@_xsi:type"]).toBe("tFormalExpression");
  });

  it("uses no prefix in @_xsi:type when definitions is undefined", () => {
    const element = run(expressionMapping, undefined);

    expect(element.dataInputAssociation[0].assignment[0].from["@_xsi:type"]).toBe("tFormalExpression");
  });

  it("does not create an assignment for non-expression mappings", () => {
    const element = run(variableMapping, definitionsWithBpmn2Prefix);

    expect(element.dataInputAssociation[0].assignment).toBeUndefined();
    expect(element.dataInputAssociation[0].sourceRef).toEqual([{ __$$text: "myVariable" }]);
  });
});

describe("setOutputDataMapping", () => {
  function run(dataMapping: DataMapping, definitions?: any) {
    const element: any = {};
    setOutputDataMapping(itemDefinitionIdByDataTypes, [dataMapping], element, element, definitions);
    return element;
  }

  it("uses the 'bpmn2:' prefix in @_xsi:type when BPMN model namespace is bound to bpmn2", () => {
    const element = run(expressionMapping, definitionsWithBpmn2Prefix);

    expect(element.dataOutputAssociation).toHaveLength(1);
    expect(element.dataOutputAssociation[0].assignment[0].to["@_xsi:type"]).toBe("bpmn2:tFormalExpression");
    expect(element.dataOutputAssociation[0].assignment[0].to.__$$text).toBe("some-expression");
  });

  it("uses no prefix in @_xsi:type when BPMN model namespace is the default xmlns", () => {
    const element = run(expressionMapping, definitionsWithDefaultNamespace);

    expect(element.dataOutputAssociation[0].assignment[0].to["@_xsi:type"]).toBe("tFormalExpression");
  });

  it("uses no prefix in @_xsi:type when definitions is undefined", () => {
    const element = run(expressionMapping, undefined);

    expect(element.dataOutputAssociation[0].assignment[0].to["@_xsi:type"]).toBe("tFormalExpression");
  });

  it("does not create an assignment for non-expression mappings", () => {
    const element = run(variableMapping, definitionsWithBpmn2Prefix);

    expect(element.dataOutputAssociation[0].assignment).toBeUndefined();
    expect(element.dataOutputAssociation[0].targetRef).toEqual({ __$$text: "myVariable" });
  });
});
