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

export const dashbuilderCompletion = `datasets:
- uuid: products
  content: >-
            [
              ["Computers", "Scanner", 5, 3],
              ["Computers", "Printer", 7, 4],
              ["Computers", "Laptop", 3, 2],
              ["Electronics", "Camera", 10, 7],
              ["Electronics", "Headphones", 5, 9]
            ]
  columns:
    - id: Section
      type: LABEL
    - id: Product
      type: LABEL
    - id: Quantity
      type: NUMBER
    - id: Quantity2
      type: NUMBER
pages:
- components:
    - html: Welcome to Dashbuilder!
      properties:
        font-size: xx-large
        margin-bottom: 30px
    - settings:
        type: BARCHART
        lookup:
            uuid: products
            group:
                - columnGroup:
                    source: Product
                  functions:
                    - source: Product
                    - source: Quantity
                      function: SUM
                    - source: Quantity2
                      function: SUM
    - settings:
        lookup:
            uuid: products`;
