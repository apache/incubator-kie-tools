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

const MINING_FIELD: string = `
[
  $v.MiningField ~> $map(function($v, $i) {
    {
      "type": "element", 
      "name": "MiningField", 
      "attributes": {
        "name": $v.name,
        "usageType": $v.usageType,
        "optype": $v.optype,
        "importance": $v.importance,
        "outliers": $v.outliers,
        "lowValue": $v.lowValue,
        "highValue": $v.highValue,
        "missingValueReplacement": $v.missingValueReplacement,
        "missingValueTreatment": $v.missingValueTreatment,
        "invalidValueTreatment": $v.invalidValueTreatment,
        "invalidValueReplacement": $v.invalidValueReplacement
      },
      "elements": $append([], [])
    }
  })
]`;

export const MINING_SCHEMA: string = `
[
  $v.MiningSchema ~> $map(function($v, $i) {
    {
      "type": "element",
      "name": "MiningSchema",
      "elements": ${MINING_FIELD}
    }
  })
]`;
