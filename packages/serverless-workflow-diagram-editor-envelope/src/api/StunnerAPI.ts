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

export interface StunnerEditor {
  session: StunnerSession;
  canvas: StunnerCanvas;
}

export interface StunnerSession {
  getGraph(): StunnerGraph;
  getEdgeByUUID(uuid: string): StunnerEdge;
  getNodeByUUID(uuid: string): StunnerNode;
  getDefinitionByElementUUID(uuid: string): Object;
  getNodeByName(name: string): StunnerNode;
  getNodeName(node: StunnerNode): string;
  getDefinitionId(bean: Object): string;
  getDefinitionName(bean: Object): string;
  getSelectedElementUUID(): string;
  getSelectedNode(): StunnerNode;
  getSelectedEdge(): StunnerEdge;
  getSelectedDefinition(): Object;
  selectByUUID(uuid: string): void;
  selectByName(name: string): void;
  clearSelection(): void;
}

export interface StunnerGraph {
  getUUID(): string;
  nodes(): Iterable<StunnerNode>;
  nodesArray(): StunnerNode[];
}

export interface StunnerElement {
  getUUID(): string;
  getContent(): StunnerContentView;
}

export interface StunnerNode extends StunnerElement {
  inConnectors(): StunnerEdge[];
  outConnectors(): StunnerEdge[];
}

export interface StunnerEdge extends StunnerElement {
  getSourceNode(): StunnerNode;
  getTargetNode(): StunnerNode;
}

export interface StunnerContentView {
  getDefinition(): Object;
}

export interface StunnerCanvas {
  getShapeIds(): string[];
  getBackgroundColor(uuid: string): string;
  setBackgroundColor(uuid: string, backgroundColor: string): void;
  getBorderColor(uuid: string): string;
  setBorderColor(uuid: string, backgroundColor: string): void;
  getLocation(uuid: string): number[];
  getAbsoluteLocation(uuid: string): number[];
  getDimensions(uuid: string): number[];
  center(uuid: string): void;
  draw(): void;
}
