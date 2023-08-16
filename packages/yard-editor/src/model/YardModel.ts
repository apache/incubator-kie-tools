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

export class YardModel {
  expressionLang: string;
  kind: string;
  inputs: Input[];
  elements: Element[];
  name: string;
  specVersion: string;

  constructor(model: any) {
    Object.assign(this, model);
  }
}

export class Element {
  logic: Logic;
  name: string;
  requirements: string[];
  type: string;
}

export class Input {
  name: string;
  type: string;
}

export interface WhenThenRule {
  when: any[];
  then: any;
}

export class Logic {
  inputs?: string[];
  expression?: string;
  outputComponents?: string[];
  rules?: WhenThenRule[] | any[][];
  type: string;
}
