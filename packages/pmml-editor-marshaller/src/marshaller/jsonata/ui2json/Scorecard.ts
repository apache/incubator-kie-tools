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

const PREDICATE: string = `[
  $v.predicate[] ~> $map(function($v, $i) {
    $ui2jsonPredicateFactory($v)
  })
]`;

const COMPLEX_PARTIAL_SCORE: string = `[
  $v.ComplexPartialScore ~> $map(function($v, $i) {
    $v
  })
]`;

const ATTRIBUTE: string = `
$v.Attribute ~> $map(function($v, $i) {
  {
    "type": "element",
    "name": "Attribute",
    "attributes": {
      "reasonCode": $v.reasonCode,
      "partialScore": $v.partialScore
    },
    "elements": $append(${PREDICATE}, ${COMPLEX_PARTIAL_SCORE})
  }
})`;

const CHARACTERISTIC: string = `[
  $v.Characteristic ~> $map(function($v, $i) {
    {
      "type": "element", 
      "name": "Characteristic", 
      "attributes": {
        "name": $v.name,
        "reasonCode": $v.reasonCode,
        "baselineScore": $v.baselineScore
      },
      "elements": $singletonArray(${ATTRIBUTE})
    }
  })
]`;

const CHARACTERISTICS: string = `
$v.Characteristics ~> $map(function($v, $i) {
  {
    "type": "element",
    "name": "Characteristics",
    "elements": $singletonArray(${CHARACTERISTIC})
  }
})`;

export const SCORE_CARD: string = `[
  models[(_type = "Scorecard")] ~> $map(function($v, $i) {
    {
      "type": "element",
      "name": "Scorecard",
      "attributes": {
        "modelName": $v.modelName,
        "functionName": $v.functionName,
        "algorithmName": $v.algorithmName,
        "initialScore": $v.initialScore,
        "useReasonCodes": $v.useReasonCodes,
        "reasonCodeAlgorithm": $v.reasonCodeAlgorithm,
        "baselineScore": $v.baselineScore,
        "baselineMethod": $v.baselineMethod,
        "isScorable": $v.isScorable
      },
      "elements": $append(${MINING_SCHEMA}, 
                    $append(${OUTPUT}, 
                      $append(${CHARACTERISTICS},
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
