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

import { XmlQName } from "@kie-tools/xml-parser-ts/dist/qNames";
import { NodeType, containment } from "./graphStructure";

export function isValidContainment({
  nodeTypes,
  inside,
  dmnObjectQName,
}: {
  nodeTypes: Set<NodeType>;
  inside: NodeType;
  dmnObjectQName: XmlQName;
}) {
  // Can't put anything inside external nodes;
  if (dmnObjectQName.prefix) {
    return false;
  }

  const allowedNodesInside = containment.get(inside);
  return [...nodeTypes].every((nodeType) => allowedNodesInside?.has(nodeType));
}
