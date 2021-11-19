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

import { DmnAutoTableI18n } from "..";
import { en as en_common } from "@kogito-tooling/i18n-common-dictionary";

export const en: DmnAutoTableI18n = {
  ...en_common,
  addParameter: "Add parameter",
  builtInAggregator: "Builtin Aggregator",
  choose: "Choose...",
  class: "class",
  clear: "Clear",
  columnOperations: {
    delete: "Delete",
    insertLeft: "Insert left",
    insertRight: "Insert right",
  },
  columns: "COLUMNS",
  context: "Context",
  contextEntry: "CONTEXT ENTRY",
  dataType: "Data Type",
  decisionRule: "DECISION RULE",
  decisionTable: "Decision Table",
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
  enterFunction: "Enter function",
  function: "Function",
  hitPolicy: "Hit Policy",
  inputClause: "INPUT CLAUSE",
  invocation: "Invocation",
  list: "List",
  literalExpression: "Literal expression",
  methodSignature: "method signature",
  model: "model",
  name: "Name",
  outputClause: "OUTPUT CLAUSE",
  parameters: "PARAMETERS",
  pmml: {
    firstSelection: "First select PMML document",
    secondSelection: "Second select PMML model",
  },
  relation: "Relation",
  rowOperations: {
    clear: "Clear",
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
