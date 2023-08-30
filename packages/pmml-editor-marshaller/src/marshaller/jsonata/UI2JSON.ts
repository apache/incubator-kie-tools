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

import { DATA_DICTIONARY } from "./ui2json/DataDictionary";
import { HEADER } from "./ui2json/Header";
import { SCORE_CARD } from "./ui2json/Scorecard";
import { REGRESSION_MODEL } from "./ui2json/RegressionModel";

const MODELS: string = `$append(
  [],
  $append(
    ${SCORE_CARD},
    ${REGRESSION_MODEL}
  )
)`;

const PMML: string = `
"elements": [
  {
    "type": "element",
    "name": "PMML",
    "attributes": {
      "xmlns": "http://www.dmg.org/PMML-4_4",
      "version": "4.4"
    },
    "elements": $append(${HEADER}, $append(${DATA_DICTIONARY}, ${MODELS}))
  }
]`;

export const UI2JSON_TRANSFORMATION: string = `(
  $bootstrap := function($node) {
    { ${PMML} }
  };

  $ui2jsonPredicateFactory := function($node) {
    $node[(_type = "SimplePredicate")]
    ? 
      {
        "type": "element",
        "name": "SimplePredicate",
        "attributes": {
          "field": $node.field,
          "operator": $node.operator,
          "value": $node.value
        }
      }
    : 
    $node[(_type = "CompoundPredicate")] ? 
      {
        "type": "element",
        "name": "CompoundPredicate",
        "attributes": {
          "booleanOperator": $node.booleanOperator
        },
        "elements": $node.predicates ~> $map(function($v, $i) {
          $ui2jsonPredicateFactory($v)
        })
      }
    : 
    $node[(_type = "True")] ? 
      {
        "type": "element",
        "name": "True"
      }
    :
    $node[(_type = "False")] ? 
      {
        "type": "element",
        "name": "False"
      }
    :
      undefined
  };

  $bootstrap($$);

)`;
