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

import { ANOMALY_DETECTION_MODEL } from "./json2ui/AnomalyDetectionModel";
import { ASSOCIATION_MODEL } from "./json2ui/AssociationModel";
import { BASELINE_MODEL } from "./json2ui/BaselineModel";
import { BAYESIAN_NETWORK_MODEL } from "./json2ui/BayesianNetworkModel";
import { CLUSTERING_MODEL } from "./json2ui/ClusteringModel";
import { DATA_DICTIONARY } from "./json2ui/DataDictionary";
import { GAUSSIAN_PROCESS_MODEL } from "./json2ui/GaussianProcessModel";
import { GENERAL_REGRESSION_MODEL } from "./json2ui/GeneralRegressionModel";
import { HEADER } from "./json2ui/Header";
import { MINING_MODEL } from "./json2ui/MiningModel";
import { NAIVE_BAYES_MODEL } from "./json2ui/NaiveBayesModel";
import { NEAREST_NEIGHBOR_MODEL } from "./json2ui/NearestNeighborModel";
import { NEURAL_NETWORK_MODEL } from "./json2ui/NeuralNetwork";
import { REGRESSION_MODEL } from "./json2ui/RegressionModel";
import { RULE_SET_MODEL } from "./json2ui/RuleSetModel";
import { SCORE_CARD } from "./json2ui/Scorecard";
import { SEQUENCE_MODEL } from "./json2ui/SequenceModel";
import { SUPPORT_VECTOR_MACHINE_MODEL } from "./json2ui/SupportVectorMachineModel";
import { TEXT_MODEL } from "./json2ui/TextModel";
import { TIME_SERIES_MODEL } from "./json2ui/TimeSeriesModel";
import { TREE_MODEL } from "./json2ui/TreeModel";

export const JSON2UI_TRANSFORMATION: string = `(
  $bootstrap := function($node) {
    { 
      ${HEADER}, 
      ${DATA_DICTIONARY}, 
      "models": $singletonArray(
        $append(
          ${ANOMALY_DETECTION_MODEL},
          $append(
            ${ASSOCIATION_MODEL},
            $append(
              ${BASELINE_MODEL},
              $append(
                ${BAYESIAN_NETWORK_MODEL},
                $append(
                  ${CLUSTERING_MODEL},
                  $append(
                    ${GAUSSIAN_PROCESS_MODEL},
                    $append(
                      ${GENERAL_REGRESSION_MODEL},
                      $append(
                        ${MINING_MODEL},
                        $append(
                          ${NAIVE_BAYES_MODEL},
                          $append(
                            ${NEAREST_NEIGHBOR_MODEL},
                            $append(
                              ${NEURAL_NETWORK_MODEL},
                              $append(
                                ${REGRESSION_MODEL},
                                $append(
                                  ${RULE_SET_MODEL},
                                  $append(
                                    ${SCORE_CARD},
                                    $append(
                                      ${SEQUENCE_MODEL},
                                      $append(
                                        ${SUPPORT_VECTOR_MACHINE_MODEL},
                                        $append(
                                          ${TEXT_MODEL},
                                          $append(
                                            ${TIME_SERIES_MODEL},
                                            ${TREE_MODEL}
                                          )
                                        )
                                      )
                                    )
                                  )
                                )
                              )
                            )
                          )
                        )
                      )
                    )
                  )
                )
              )
            )
          )
        )
      )
    }
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
