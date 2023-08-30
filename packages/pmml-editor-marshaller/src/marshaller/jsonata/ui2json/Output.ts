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

export const OUTPUT: string = `[
  $v.Output ~> $map(function($v, $i) {
    {
      "type": "element",
      "name": "Output",
      "elements": ${OUTPUT_FIELD}
    }
  })
]`;
