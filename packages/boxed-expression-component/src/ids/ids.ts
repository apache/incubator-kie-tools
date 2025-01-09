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

import {
  meta as dmn15meta,
  elements as dmn15elements,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/meta";
import { XmlParserTsIdRandomizer } from "@kie-tools/xml-parser-ts/dist/idRandomizer";
import { BoxedExpression, generateUuid } from "../api";

export function findAllIdsDeep(expression: BoxedExpression | undefined): Set<string> {
  if (!expression) {
    return new Set();
  }

  return getIdRandomizerForExpression(expression).getOriginalIds();
}

export function mutateExpressionRandomizingIds(expression: BoxedExpression | undefined): Map<string, string> {
  if (!expression) {
    return new Map();
  }

  return getIdRandomizerForExpression(expression).randomize({ skipAlreadyAttributedIds: false }).getRandomized();
}

function getNewBeeIdRandomizer() {
  return new XmlParserTsIdRandomizer({
    meta: dmn15meta,
    elements: dmn15elements,
    newIdGenerator: generateUuid,
    matchers: [],
  });
}

function getIdRandomizerForExpression(expression: BoxedExpression) {
  return getNewBeeIdRandomizer().ack({
    json: { __$$element: "decision", expression },
    type: "DMN15__tDecision",
    attr: "expression",
  });
}
