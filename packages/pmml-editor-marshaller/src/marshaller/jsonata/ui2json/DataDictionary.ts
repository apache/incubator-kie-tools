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

const INTERVAL: string = `
[
  $v.Interval ~> $map(function($v, $i) {
    {
      "type": "element", 
      "name": "Interval", 
      "attributes": {
        "closure": $v.closure,
        "leftMargin": $v.leftMargin,
        "rightMargin": $v.rightMargin
      }
    }
  })
]`;

const VALUE: string = `
[
  $v.Value ~> $map(function($v, $i) {
    {
      "type": "element", 
      "name": "Value", 
      "attributes": {
        "value": $v.value,
        "displayValue": $v.displayValue,
        "property": $v.property
      }
    }
  })
]`;

const DATA_FIELD: string = `
[
  $v.DataField ~> $map(function($v, $i) {
    {
      "type": "element", 
      "name": "DataField", 
      "attributes": {
        "name": $v.name,
        "displayName": $v.displayName,
        "optype": $v.optype,
        "dataType": $v.dataType,
        "taxonomy": $v.taxonomy,
        "isCyclic": $v.isCyclic
      },
      "elements": $append(${INTERVAL}, ${VALUE})
    }
  })
]`;

export const DATA_DICTIONARY: string = `
[
  DataDictionary ~> $map(function($v, $i) {
    {
      "type": "element",
      "name": "DataDictionary",
      "attributes": {
        "numberOfFields": DataDictionary.DataField ? $count(DataDictionary.DataField) : undefined
      },
      "elements": ${DATA_FIELD}
    }
  })
]`;
