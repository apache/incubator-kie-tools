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

export const HEADER: string = `
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
