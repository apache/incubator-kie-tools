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
  contextExpression: {
    variable: "variable",
    expression: "expression",
  },
  yourAnnotationsHere: "// Your annotations here",
  hitPolicyLabel: "Hit policy",
  aggregatorFunction: "Aggregator function",
  logicTypeHelp: {
    literal:
      "A boxed literal expression in DMN is a literal FEEL expression as text in a table cell, typically with a labeled column and an assigned data type.",
    context:
      "A boxed context expression in DMN is a set of variable names and values with a result value. Each name-value pair is a context entry.",
    decisionTable:
      "A decision table in DMN is a visual representation of one or more business rules in a tabular format.",
    relation:
      "A boxed relation expression in DMN is a traditional data table with information about given entities, listed as rows. You use boxed relation tables to define decision data for relevant entities in a decision at a particular node.",
    functionDefinition:
      "A boxed function expression in DMN is a parameterized boxed expression containing a literal FEEL expression, a nested context expression of an external JAVA or PMML function, or a nested boxed expression of any type.",
    invocation:
      "A boxed invocation expression in DMN is a boxed expression that invokes a business knowledge model. A boxed invocation expression contains the name of the business knowledge model to be invoked and a list of parameter bindings.",
    list: "A boxed list expression in DMN represents a FEEL list of items. You use boxed lists to define lists of relevant items for a particular node in a decision.",
    conditional:
      'A boxed conditional offers a visual representation of an if statement using three rows. The expression in the "if" part MUST resolve to a boolean.',
    for: `A boxed iterator offers a visual representation of an iterator statement. For the "for" loop, the right part of the "for" displays the iterator variable name. The second row holds an expression representing the collection that will be iterated over. The expression in the "in" row MUST resolve to a collection. The last row contains the expression that will process each element of the collection.`,
    every: `A boxed iterator offers a visual representation of an iterator statement. For the "every" loop, the right part of the "every" displays the iterator variable name. The second row holds an expression representing the collection that will be iterated over. The expression in the "in" row MUST resolve to a collection. The last line is an expression that will be evaluated on each item. The expression defined in the "satisfies" MUST resolve to a boolean.`,
    some: `A boxed iterator offers a visual representation of an iterator statement. For the "some" loop, the right part of the "some" displays the iterator variable name. The second row holds an expression representing the collection that will be iterated over. The expression in the "in" row MUST resolve to a collection. The last line is an expression that will be evaluated on each item. The expression defined in the "satisfies" MUST resolve to a boolean.`,
    filter:
      "A boxed filter offers a visual representation of collection filtering. The top part is an expression that is the collection to be filtered. The bottom part, between the square brackets, holds the filter expression.",
  },
  pasteOperationNotSuccessful: "Paste operation was not successful",
  functionKindHelp: {
    feel: "Define function as a 'Friendly Enough Expression Language (FEEL)' expression. This is the default.",
    pmml: "Define 'Predictive Model Markup Language (PMML)' model to invoke.\nEditor parses and offers you all your PMML models from the workspace.",
    java: "Define the full qualified java class name and a public static method signature to invoke.\nThe method signature consists of the name of the method, followed by an argument list of the argument types.",
    notSupported: "Not supported",
  },
  label: "label",
  value: "value",
  classNameLabel: "Class name",
  methodSignatureLabel: "Method signature",
  getLabelexample: (label: string) => `${label} example`,
  parameterNamePlaceholder: "Parameter Name",
  noneSelected: "-- None selected --",
  selectDocument: "Select a document first",
  parameter: "parameter",
  functionName: "Function name",
  child: "child",
  iterableRowLabel: {
    for: "for",
    some: "some",
    every: "every",
    return: "return",
    in: "in",
    satisfies: "satisfies",
  },
};
