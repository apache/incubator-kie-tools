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

const COMPLEX_PARTIAL_SCORE: string = `
  "ComplexPartialScore": $v.elements[(name = "ComplexPartialScore")] ~> $map(function($v, $i) {
    $v
})`;

const ATTRIBUTE: string = `
{
  "Attribute": [$v.elements[(name = "Attribute")] ~> $map(function($v, $i) {
    $merge([
      $v.attributes,
      {
        "partialScore": $number($v.attributes.partialScore),
        "predicate": $json2uiPredicateFactory($v.elements[0]),
        ${COMPLEX_PARTIAL_SCORE}        
      }
    ])
  })]
}`;

const CHARACTERISTICS: string = `
"Characteristics": {
  "Characteristic": [$v.elements[(name = "Characteristics")].elements[(name = "Characteristic")] ~> $map(function($v, $i) {
    $merge([
      $v.attributes,
      {
        "baselineScore": $number($v.attributes.baselineScore)
      },
      ${ATTRIBUTE}
    ])
  })]
}`;

const OUTPUT: string = `
"Output": {
  "OutputField": [$v.elements[(name = "Output")].elements[(name = "OutputField")] ~> $map(function($v, $i) {
    $merge([
      $v.attributes,
      {
        "rank": $number($v.attributes.rank)
      }
    ])
  })] 
}`;

const MODEL_STATS: string = `
  "ModelStats": $v.elements[(name = "ModelStats")] ~> $map(function($v, $i) {
    $v
})`;

const MODEL_EXPLANATION: string = `
  "ModelExplanation": $v.elements[(name = "ModelExplanation")] ~> $map(function($v, $i) {
    $v
})`;

const MODEL_VERIFICATION: string = `
  "ModelVerification": $v.elements[(name = "ModelVerification")] ~> $map(function($v, $i) {
    $v
})`;

const TARGETS: string = `
  "Targets": $v.elements[(name = "Targets")] ~> $map(function($v, $i) {
    $v
})`;

const LOCAL_TRANSFORMATION: string = `
  "LocalTransformations": $v.elements[(name = "LocalTransformations")] ~> $map(function($v, $i) {
    $v
})`;

export const SCORE_CARD: string = `
elements.elements[(name = "Scorecard")] ~> $map(function($v, $i) {
  $merge([
    $scorecardFactory(),
    $v.attributes,
    {
      "_type": $v.name
    },
    {
      ${MINING_SCHEMA}, 
      ${OUTPUT},
      ${CHARACTERISTICS}, 
      ${MODEL_STATS},
      ${MODEL_EXPLANATION},
      ${MODEL_VERIFICATION},
      ${TARGETS},
      ${LOCAL_TRANSFORMATION}
    }
  ])
})`;
