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

import { Characteristics, MiningSchema, Scorecard } from "../../model/pmml4_4";
import { LOCAL_TRANSFORMATIONS } from "./LocalTransformations";
import { MINING_SCHEMA } from "./MiningSchema";
import { MODEL_EXPLANATION } from "./ModelExplanation";
import { MODEL_STATS } from "./ModelStats";
import { MODEL_VERIFICATION } from "./ModelVerification";
import { OUTPUT } from "./Output";
import { TARGETS } from "./Targets";

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

export const SCORE_CARD: string = `
elements.elements[(name = "Scorecard")] ~> $map(function($v, $i) {
  $merge([
    $scorecardFactory(),
    $v.attributes,
    {
      "isScorable": $eval($v.attributes.isScorable),
      "useReasonCodes": $eval($v.attributes.useReasonCodes),
      "initialScore": $number($v.attributes.initialScore),
      "baselineScore": $number($v.attributes.baselineScore),
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
      ${LOCAL_TRANSFORMATIONS}
    }
  ])
})`;

//Construction of a Scorecard data-structure can be peformed in the JSONata mapping however
//TypeScript's instanceof operator relies on the applicable constructor function having been
//called and therefore we must instantiate the object itself. Furthermore the recurrsive hieracical
//nature of CompoundPredicates cannot be handled by a JSONata mapping.
export function scorecardFactory(): Scorecard {
  return new Scorecard({
    MiningSchema: new MiningSchema({ MiningField: [] }),
    Characteristics: new Characteristics({ Characteristic: [] }),
    functionName: "regression",
  });
}
