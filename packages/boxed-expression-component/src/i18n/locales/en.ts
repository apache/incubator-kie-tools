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

import { BoxedExpressionEditorI18n } from "..";
import { en as en_common } from "@kie-tools/i18n-common-dictionary";

export const en: BoxedExpressionEditorI18n = {
  ...en_common,
  addParameter: "Add parameter",
  builtInAggregator: "Builtin Aggregator",
  builtInAggregatorHelp: {
    sum: "Outputs the sum of all collected values. Values must be numeric.",
    count: "Outputs the number of matching rules.",
    min: "Outputs the minimum value among the matches. The resulting values must be comparable, such as numbers, dates or text (lexicographic order).",
    max: "Outputs the maximum value among the matches. The resulting values must be comparable, such as numbers, dates or text (lexicographic order).",
    none: "Aggregates values in an arbitrary list.",
  },
  choose: "Choose...",
  class: "class",
  columnOperations: {
    delete: "Delete",
    insertLeft: "Insert left",
    insertRight: "Insert right",
  },
  columns: "COLUMNS",
  context: "Context",
  contextEntry: "CONTEXT ENTRY",
  dataType: "Data Type",
  dataTypeDropDown: {
    builtIn: "BUILT-IN",
    custom: "CUSTOM",
  },
  decisionRule: "DECISION RULE",
  decisionTable: "Decision table",
  delete: "Delete",
  document: "document",
  editClause: {
    input: "Edit Input Clause",
    output: "Edit Output Clause",
  },
  editContextEntry: "Edit Context Entry",
  editExpression: "Edit Expression",
  editHitPolicy: "Edit Hit Policy",
  editParameter: "Edit Parameter",
  editParameters: "Edit parameters",
  editRelation: "Edit Relation",
  enterFunction: "Function name",
  enterText: "Enter Text",
  expression: "Expression",
  function: "Function",
  hitPolicy: "Hit Policy",
  hitPolicyHelp: {
    unique: "Permits only one rule to match. Any overlap raises an error.",
    first: "Uses the first match in rule order.",
    priority:
      "Permits multiple rules to match, with different outputs. The output that comes first in the output values list is selected.",
    any: "Permits multiple rules to match, but they must all have the same output. If multiple matching rules do not have the same output, an error is raised.",
    collect: "Aggregates output from multiple rules based on an aggregation function.",
    ruleOrder:
      "Collects output from multiple rules into list ordered according to rules order. It is very similar as 'Collect' without any aggregation function, but with explicit consistent ordering in the final list as defined in the table.",
    outputOrder:
      "Collects output from multiple rules into list ordered using the same sorting mechanism as 'Priority' hit policy.",
  },
  inputClause: "INPUT CLAUSE",
  invocation: "Invocation",
  insert: "Insert",
  insertDirections: {
    above: "Above",
    below: "Below",
    toTheLeft: "To the left",
    toTheRight: "To the right",
  },
  list: "List",
  literal: "Literal",
  manage: "Manage",
  methodSignature: "method signature",
  model: "model",
  name: "Name",
  noParametersDefined: "No parameters have been defined.",
  outputClause: "OUTPUT CLAUSE",
  parameters: "PARAMETERS",
  pmml: {
    firstSelection: "First select PMML document",
    secondSelection: "Second select PMML model",
  },
  relation: "Relation",
  rowOperations: {
    reset: "Reset",
    delete: "Delete",
    duplicate: "Duplicate",
    insertAbove: "Insert above",
    insertBelow: "Insert below",
  },
  rows: "ROWS",
  ruleAnnotation: "RULE ANNOTATION",
  selectExpression: "Select expression",
  selectFunctionKind: "Select Function Kind",
  selectLogicType: "Select logic type",
};
