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
import { DATA_DICTIONARY } from "./json2ui/DataDictionary";
import { HEADER } from "./json2ui/Header";
import { SCORE_CARD } from "./json2ui/Scorecard";

export const JSON2UI_TRANSFORMATION: string = `(
  $bootstrap := function($node) {
    { 
      ${HEADER}, 
      ${DATA_DICTIONARY}, 
      "models": $singletonArray($append([], ${SCORE_CARD})) }
  };

  $json2uiPredicateFactory := function($node) {
    $node[(name = "SimplePredicate")] 
    ? 
      $merge([
        $json2uiSimplePredicateFactory(), 
        $node.attributes,
        {
          "_type": $node.name
        }
      ]) 
    : 
    $node[(name = "CompoundPredicate")] 
    ? 
      $merge([
        $json2uiCompoundPredicateFactory(), 
        $node.attributes,
        {
          "_type": $node.name,
          "predicates": $node.elements ~> $map(function($v, $i) {
            $json2uiPredicateFactory($v)
          })
        }
      ]) 
    :
    $node[(name = "True")] 
    ?
      $merge([
        $json2uiTruePredicateFactory(),
        {
          "_type": $node.name
        }
      ])
    :
    $node[(name = "False")] 
    ?
      $merge([
        $json2uiFalsePredicateFactory(),
        {
          "_type": $node.name
        }
      ])
    :
      undefined
  };

  $bootstrap($$);
)`;
