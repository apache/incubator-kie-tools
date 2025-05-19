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

import { ReferenceDictionary } from "@kie-tools-core/i18n/dist/core";
import { CommonI18n } from "@kie-tools/i18n-common-dictionary";

interface BoxedExpressionEditorDictionary extends ReferenceDictionary {
  addParameter: string;
  builtInAggregator: string;
  builtInAggregatorHelp: {
    sum: string;
    count: string;
    min: string;
    max: string;
    none: string;
  };
  choose: string;
  columns: string;
  columnOperations: {
    delete: string;
    insertLeft: string;
    insertRight: string;
  };
  class: string;
  context: string;
  contextEntry: string;
  dataType: string;
  dataTypeDropDown: {
    builtIn: string;
    custom: string;
  };
  decisionRule: string;
  decisionTable: string;
  document: string;
  editClause: {
    input: string;
    output: string;
  };
  editContextEntry: string;
  editParameter: string;
  editRelation: string;
  enterFunction: string;
  enterText: string;
  expression: string;
  delete: string;
  function: string;
  hitPolicy: string;
  hitPolicyHelp: {
    unique: string;
    first: string;
    priority: string;
    any: string;
    collect: string;
    ruleOrder: string;
    outputOrder: string;
  };
  inputClause: string;
  invocation: string;
  insert: string;
  insertDirections: {
    toTheRight: string;
    toTheLeft: string;
    above: string;
    below: string;
  };
  list: string;
  literal: string;
  manage: string;
  methodSignature: string;
  model: string;
  name: string;
  noParametersDefined: string;
  parameters: string;
  outputClause: string;
  pmml: {
    firstSelection: string;
    secondSelection: string;
  };
  relation: string;
  rows: string;
  rowOperations: {
    reset: string;
    delete: string;
    duplicate: string;
    insertAbove: string;
    insertBelow: string;
  };
  ruleAnnotation: string;
  selectExpression: string;
  selectLogicType: string;
}

export interface BoxedExpressionEditorI18n extends BoxedExpressionEditorDictionary, CommonI18n {}
