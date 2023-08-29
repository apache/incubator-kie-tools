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

import { LOCAL_TRANSFORMATIONS } from "./LocalTransformations";
import { MINING_SCHEMA } from "./MiningSchema";
import { MODEL_EXPLANATION } from "./ModelExplanation";
import { MODEL_STATS } from "./ModelStats";
import { MODEL_VERIFICATION } from "./ModelVerification";
import { OUTPUT } from "./Output";
import { TARGETS } from "./Targets";

const NUMERIC_PREDICTOR: string = `[
  $v.NumericPredictor ~> $map(function($v, $i) {
    {
      "type": "element", 
      "name": "NumericPredictor", 
      "attributes": {
        "name": $v.name,
        "exponent": $v.exponent,
        "coefficient": $v.coefficient
      }
    }
  })
]`;

const CATEGORICAL_PREDICTOR: string = `[
  $v.CategoricalPredictor ~> $map(function($v, $i) {
    {
      "type": "element", 
      "name": "CategoricalPredictor", 
      "attributes": {
        "name": $v.name,
        "value": $v.value,
        "coefficient": $v.coefficient
      }
    }
  })
]`;

const PREDICTOR_TERM: string = `[
  $v.PredictorTerm ~> $map(function($v, $i) {
    $v
  })
]`;

const REGRESSION_TABLE: string = `[
  $v.RegressionTable ~> $map(function($v, $i) {
    {
      "type": "element", 
      "name": "RegressionTable", 
      "attributes": {
        "intercept": $v.intercept,
        "targetCategory": $v.targetCategory 
      },
      "elements": $append(${NUMERIC_PREDICTOR},
                    $append(${CATEGORICAL_PREDICTOR},
                      $append([], ${PREDICTOR_TERM})
                    )
                  )
    }
  })
]`;

export const REGRESSION_MODEL: string = `[
  models[(_type = "RegressionModel")] ~> $map(function($v, $i) {
    {
      "type": "element",
      "name": "RegressionModel",
      "attributes": {
        "modelName": $v.modelName,
        "functionName": $v.functionName,
        "algorithmName": $v.algorithmName,
        "modelType": $v.modelType,
        "targetFieldName": $v.targetFieldName,
        "normalizationMethod": $v.normalizationMethod,
        "isScorable": $v.isScorable
      },
      "elements": $append(${MINING_SCHEMA},
                    $append(${OUTPUT}, 
                      $append(${REGRESSION_TABLE}, 
                        $append(${MODEL_STATS},
                          $append(${MODEL_EXPLANATION},
                            $append(${MODEL_VERIFICATION},
                              $append(${TARGETS},
                                $append([], ${LOCAL_TRANSFORMATIONS})
                              )
                            )
                          )
                        )
                      )
                    )
                  )
    }
  })
]`;
