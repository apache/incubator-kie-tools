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

import { MiningSchema, RegressionModel, RegressionTable } from "../../model/pmml4_4";
import { LOCAL_TRANSFORMATIONS } from "./LocalTransformations";
import { MINING_SCHEMA } from "./MiningSchema";
import { MODEL_EXPLANATION } from "./ModelExplanation";
import { MODEL_STATS } from "./ModelStats";
import { MODEL_VERIFICATION } from "./ModelVerification";
import { OUTPUT } from "./Output";
import { TARGETS } from "./Targets";

const NUMERIC_PREDICTOR: string = `
"NumericPredictor": $singletonArray(
  $v.elements[(name = "NumericPredictor")] ~> $map(function($v, $i) {
    $merge([
      $v.attributes,
      {
        "exponent": $number($v.attributes.exponent),
        "coefficient": $number($v.attributes.coefficient)
      }
    ])
  })
)`;

const CATEGORICAL_PREDICTOR: string = `
"CategoricalPredictor": $singletonArray(
  $v.elements[(name = "CategoricalPredictor")] ~> $map(function($v, $i) {
    $merge([
      $v.attributes,
      {
        "coefficient": $number($v.attributes.coefficient)
      }
    ])
  })
)`;

const PREDICTOR_TERM: string = `
  "PredictorTerm": $v.elements[(name = "PredictorTerm")] ~> $map(function($v, $i) {
    $v
})`;

const REGRESSION_TABLE: string = `
"RegressionTable": $singletonArray(
  $v.elements[(name = "RegressionTable")] ~> $map(function($v, $i) {
    $merge([
      $v.attributes,
      {
        "intercept": $number($v.attributes.intercept)
      },
      {
        ${NUMERIC_PREDICTOR},
        ${CATEGORICAL_PREDICTOR},
        ${PREDICTOR_TERM}
      }
    ])
  })
)`;

export const REGRESSION_MODEL: string = `
elements.elements[(name = "RegressionModel")] ~> $map(function($v, $i) {
  $merge([
    $regressionModelFactory(),
    $v.attributes,
    {
      "_type": $v.name
    },
    {
      ${MINING_SCHEMA}, 
      ${OUTPUT},
      ${MODEL_STATS},
      ${MODEL_EXPLANATION},
      ${MODEL_VERIFICATION},
      ${TARGETS},
      ${LOCAL_TRANSFORMATIONS},
      ${REGRESSION_TABLE}
    }
  ])
})`;

//Construction of a RegressionModel data-structure can be peformed in the JSONata mapping however
//TypeScript's instanceof operator relies on the applicable constructor function having been
//called and therefore we must instantiate the object itself.
export function regressionModelFactory(): RegressionModel {
  return new RegressionModel({
    MiningSchema: new MiningSchema({ MiningField: [] }),
    RegressionTable: [new RegressionTable({ intercept: 0.0 })],
    functionName: "regression",
  });
}
