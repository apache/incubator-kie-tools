/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { MINING_SCHEMA } from "./MiningSchema";

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
      "elements": ${ATTRIBUTE}
    }
  })
]`;

const CHARACTERISTICS: string = `
$v.Characteristics ~> $map(function($v, $i) {
  {
    "type": "element",
    "name": "Characteristics",
    "elements": ${CHARACTERISTIC}
  }
})`;

const OUTPUT_FIELD: string = `[
  $v.OutputField ~> $map(function($v, $i) {
    {
      "type": "element", 
      "name": "OutputField", 
      "attributes": {
        "name": $v.name,
        "displayName": $v.displayName,
        "optype": $v.optype,
        "dataType": $v.dataType,
        "targetField": $v.targetField,
        "feature": $v.feature,
        "value": $v.value,
        "ruleFeature": $v.ruleFeature,
        "algorithm": $v.algorithm,
        "rank": $v.rank,
        "rankBasis": $v.rankBasis,
        "rankOrder": RankOrder$v.rankOrder,
        "isMultiValued": $v.isMultiValued, 
        "segmentId": $v.segmentId,
        "isFinalResult": $v.isFinalResult      
      },
      "elements": $append([], [])
    }
  })
]`;

const OUTPUT: string = `[
  $v.Output ~> $map(function($v, $i) {
    {
      "type": "element",
      "name": "Output",
      "elements": ${OUTPUT_FIELD}
    }
  })
]`;

export const MODELS_SCORECARD: string = `[
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
      "elements": $append(${MINING_SCHEMA}, $append(${OUTPUT}, ${CHARACTERISTICS}))
    }
  })
]`;
