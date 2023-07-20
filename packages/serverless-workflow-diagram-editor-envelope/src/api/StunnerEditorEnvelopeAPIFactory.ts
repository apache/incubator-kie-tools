/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { StunnerEdge, StunnerNode } from "./StunnerAPI";
import { Definition, Edge, Node } from "./StunnerEditorEnvelopeAPI";

export function createNode(node: StunnerNode, mapper: DefinitionMapper) {
  const jsNode = new Node();
  jsNode.uuid = node.getUUID();
  jsNode.definition = createDefinition(node.getContent().getDefinition(), mapper);
  jsNode.inEdges = node.inConnectors().map((edge) => createEdge(edge, mapper));
  jsNode.outEdges = node.outConnectors().map((edge) => createEdge(edge, mapper));
  return jsNode;
}

export function createEdge(edge: StunnerEdge, mapper: DefinitionMapper) {
  const jsEdgr: Edge = new Edge();
  jsEdgr.uuid = edge.getUUID();
  jsEdgr.definition = createDefinition(edge.getContent().getDefinition(), mapper);
  jsEdgr.source = edge.getSourceNode().getUUID();
  jsEdgr.target = edge.getTargetNode().getUUID();
  return jsEdgr;
}

export function createDefinition(bean: Object, mapper: DefinitionMapper) {
  const result: Definition = new Definition();
  result.id = mapper.getId(bean);
  result.name = mapper.getName(bean);
  return result;
}

export interface DefinitionMapper {
  getId(bean: Object): string;
  getName(bean: Object): string;
}
