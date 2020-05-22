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

const APPLICATION: string = `
[
  Header.Application ~> $map(function($v, $i) {
    {
      "type": "element", 
      "name": "Application", 
      "attributes": {
        "name": Header.Application.name,
        "version": Header.Application.version
      }
    }
  })
]`;

const ANNOTATION: string = `
[
  Header.Annotation ~> $map(function($v, $i) {
    {
      "type": "element", 
      "name": "Annotation", 
      "elements": [$v]
    }
  })
]`;

const TIMESTAMP: string = `
[
  Header.Timestamp ~> $map(function($v, $i) {
    {
      "type": "element", 
      "name": "Timestamp",
      "elements": [$v]
    }
  })
]`;

const HEADER: string = `
[
  Header ~> $map(function($v, $i) {
    {
      "type": "element",
      "name": "Header",
      "attributes": {
        "copyright": Header.copyright, 
        "description": Header.description, 
        "modelVersion": Header.modelVersion
      },
      "elements": $append(${APPLICATION}, $append(${ANNOTATION}, ${TIMESTAMP}))
    }
  })
]`;

const DATA_FIELD: string = `
[
  DataDictionary.DataField ~> $map(function($v, $i) {
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
      "elements": $append([
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
                          ],
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
                          ])
    }
  })
]`;

const DATA_DICTIONARY: string = `
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

const PMML: string = `
"elements": [
  {
    "type": "element",
    "name": "PMML",
    "attributes": {
      "xmlns": "http://www.dmg.org/PMML-4_4",
      "version": "4.4"
    },
    "elements": $append(${HEADER}, ${DATA_DICTIONARY})
  }
]`;

export const TRANSFORMATION: string = `{ ${PMML} }`;
