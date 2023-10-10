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

import * as JSONata from "jsonata";
import { Expression } from "jsonata";
import * as XMLJS from "xml-js";
import { JSON2UI_TRANSFORMATION as json2ui } from "./jsonata/JSON2UI";
import { anomalyDetectionModelFactory } from "./jsonata/json2ui/AnomalyDetectionModel";
import { associationModelFactory } from "./jsonata/json2ui/AssociationModel";
import { baselineModelFactory } from "./jsonata/json2ui/BaselineModel";
import { bayesianNetworkModelFactory } from "./jsonata/json2ui/BayesianNetworkModel";
import { clusteringModelFactory } from "./jsonata/json2ui/ClusteringModel";
import { gaussianProcessModelFactory } from "./jsonata/json2ui/GaussianProcessModel";
import { generalRegressionModelFactory } from "./jsonata/json2ui/GeneralRegressionModel";
import { miningModelFactory } from "./jsonata/json2ui/MiningModel";
import { naiveBayesModelFactory } from "./jsonata/json2ui/NaiveBayesModel";
import { nearestNeighborModelFactory } from "./jsonata/json2ui/NearestNeighborModel";
import { neuralNetworkFactory } from "./jsonata/json2ui/NeuralNetwork";
import { regressionModelFactory } from "./jsonata/json2ui/RegressionModel";
import { ruleSetModelFactory } from "./jsonata/json2ui/RuleSetModel";
import { scorecardFactory } from "./jsonata/json2ui/Scorecard";
import { sequenceModelFactory } from "./jsonata/json2ui/SequenceModel";
import { supportVectorMachineModelFactory } from "./jsonata/json2ui/SupportVectorMachineModel";
import { textModelFactory } from "./jsonata/json2ui/TextModel";
import { timeSeriesModelFactory } from "./jsonata/json2ui/TimeSeriesModel";
import { treeModelFactory } from "./jsonata/json2ui/TreeModel";
import { UI2JSON_TRANSFORMATION as ui2json } from "./jsonata/UI2JSON";
import { CompoundPredicate, False, PMML, SimplePredicate, True } from "./model/pmml4_4";

export function XML2PMML(xml: string): PMML {
  const doc: XMLJS.Element = XMLJS.xml2js(xml) as XMLJS.Element;
  const expression: Expression = JSONata(json2ui);
  expression.registerFunction("merge", merge);
  expression.registerFunction("singletonArray", singletonArray);

  expression.registerFunction("anomalyDetectionModelFactory", anomalyDetectionModelFactory);
  expression.registerFunction("associationModelFactory", associationModelFactory);
  expression.registerFunction("baselineModelFactory", baselineModelFactory);
  expression.registerFunction("bayesianNetworkModelFactory", bayesianNetworkModelFactory);
  expression.registerFunction("clusteringModelFactory", clusteringModelFactory);
  expression.registerFunction("gaussianProcessModelFactory", gaussianProcessModelFactory);
  expression.registerFunction("generalRegressionModelFactory", generalRegressionModelFactory);
  expression.registerFunction("miningModelFactory", miningModelFactory);
  expression.registerFunction("naiveBayesModelFactory", naiveBayesModelFactory);
  expression.registerFunction("nearestNeighborModelFactory", nearestNeighborModelFactory);
  expression.registerFunction("neuralNetworkFactory", neuralNetworkFactory);
  expression.registerFunction("regressionModelFactory", regressionModelFactory);
  expression.registerFunction("ruleSetModelFactory", ruleSetModelFactory);
  expression.registerFunction("scorecardFactory", scorecardFactory);
  expression.registerFunction("sequenceModelFactory", sequenceModelFactory);
  expression.registerFunction("supportVectorMachineModelFactory", supportVectorMachineModelFactory);
  expression.registerFunction("textModelFactory", textModelFactory);
  expression.registerFunction("timeSeriesModelFactory", timeSeriesModelFactory);
  expression.registerFunction("treeModelFactory", treeModelFactory);

  expression.registerFunction("json2uiSimplePredicateFactory", json2uiSimplePredicateFactory);
  expression.registerFunction("json2uiCompoundPredicateFactory", json2uiCompoundPredicateFactory);
  expression.registerFunction("json2uiTruePredicateFactory", json2uiTruePredicateFactory);
  expression.registerFunction("json2uiFalsePredicateFactory", json2uiFalsePredicateFactory);

  const pmml: PMML = expression.evaluate(doc);

  return pmml;
}

//See https://github.com/jsonata-js/jsonata/issues/457
//JSONata's $merge([...]) function merges data but not prototype definitions. This specialised version
//merges data into a clone of the first object passed in the argument array.
function merge(arg: any[]): any {
  if (typeof arg === undefined) {
    return undefined;
  }

  //See https://stackoverflow.com/a/728400
  //We need to clone the first object being merged AND preserve it's prototype definition.
  //Use of Object.create(..) extends the provided object (in the prototype chain) and hence fails immer's isDraftable check.
  //Use of Object.assign(..) or {...arg[0]} does not preserve the prototype chain.
  const result: any = clone(arg[0]);

  arg.forEach((obj: any) => {
    for (const prop in obj) {
      result[prop] = obj[prop];
    }
  });

  return result;
}

function clone(obj: any) {
  if (obj === null || typeof obj !== "object") {
    return obj;
  }
  const temp: any = new obj.constructor(obj);
  for (const key in obj) {
    temp[key] = clone(obj[key]);
  }
  return temp;
}

//See https://github.com/jsonata-js/jsonata/issues/218
//JSONata returns a single object instead of a single item array by design.
//This function fixes that by ensuring an array of values is always returned.
function singletonArray(value: any): any[] {
  if (value === undefined || value === null) {
    return [];
  }
  if (Array.isArray(value)) {
    return value;
  }
  return [value];
}

function json2uiSimplePredicateFactory(): SimplePredicate {
  return new SimplePredicate({ field: "", operator: "equal" });
}

function json2uiCompoundPredicateFactory(): CompoundPredicate {
  return new CompoundPredicate({ booleanOperator: "and" });
}

function json2uiTruePredicateFactory(): True {
  return new True({});
}

function json2uiFalsePredicateFactory(): False {
  return new False({});
}

export function PMML2XML(pmml: PMML): string {
  const expression: Expression = JSONata(ui2json);
  expression.registerFunction("singletonArray", singletonArray);

  const json: any = expression.evaluate(pmml);
  const xml: string = XMLJS.js2xml(json, { spaces: 2 });

  return xml;
}
