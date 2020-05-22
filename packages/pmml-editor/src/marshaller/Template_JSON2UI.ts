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

const HEADER: string = `
"Header": elements.elements[(name = "Header")] ~> $map(function($v, $i) {
  {
    "copyright": elements.elements[(name = "Header")].attributes.copyright,
    "description": elements.elements[(name = "Header")].attributes.description,
    "modelVersion": elements.elements[(name = "Header")].attributes.modelVersion,
    "Application": elements.elements[(name = "Header")].elements[(name = "Application")].attributes,
    "Annotation": elements.elements[(name = "Header")].elements[(name = "Annotation")].elements,
    "Timestamp": elements.elements[(name = "Header")].elements[(name = "Timestamp")].elements[0]
  }
})`;

const DATA_DICTIONARY: string = `
"DataDictionary": elements.elements[(name = "DataDictionary")] ~> $map(function($v, $i) {
  $merge(
    [
      {}, 
      {
        "numberOfFields": $number($v.attributes.numberOfFields),
        "DataField": $singletonArray(
          $v.elements[(name = "DataField")] ~> $map(function($v, $i) {
            $merge(
              [
                {},
                {
                  "Interval": $singletonArray(
                    $v.elements[(name = "Interval")].attributes ~> $map(function($v, $i) {
                      { "closure": $v.closure,
                        "leftMargin": $number($v.leftMargin),
                        "rightMargin": $number($v.rightMargin)
                      }
                    })),
                  "Value": [$v.elements[(name = "Value")].attributes]
                }, 
                $v.attributes
              ])
            })
          )
      }
    ])
  })`;

const SCORE_CARD: string = `
"Scorecard": elements.elements[(name = "Scorecard")] ~> $map(function($v, $i) {
  $merge([{}, $v.attributes])
})`;

export const TRANSFORMATION: string = `{ ${HEADER}, ${DATA_DICTIONARY}, ${SCORE_CARD} }`;
