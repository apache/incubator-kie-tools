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

export interface StunnerEditorEnvelopeAPI {
  // Session API.
  editor_session_getAllNodesUUID(): Promise<string[]>;
  editor_session_getEdgeByUUID(uuid: string): Promise<Edge>;
  editor_session_getNodeByUUID(uuid: string): Promise<Node>;
  editor_session_getDefinitionByElementUUID(uuid: string): Promise<Object>;
  editor_session_getNodeByName(name: string): Promise<Node>;
  editor_session_getNodeName(node: Node): Promise<string>;
  editor_session_getSelectedElementUUID(): Promise<string>;
  editor_session_getSelectedNode(): Promise<Node>;
  editor_session_getSelectedEdge(): Promise<Edge>;
  editor_session_getSelectedDefinition(): Promise<Object>;
  editor_session_selectByUUID(uuid: string): Promise<void>;
  editor_session_selectByName(name: string): Promise<void>;
  editor_session_clearSelection(): Promise<void>;
  // Canvas API.
  editor_canvas_getShapeIds(): Promise<string[]>;
  editor_canvas_getBackgroundColor(uuid: string): Promise<string>;
  editor_canvas_setBackgroundColor(uuid: string, backgroundColor: string): Promise<void>;
  editor_canvas_getBorderColor(uuid: string): Promise<string>;
  editor_canvas_setBorderColor(uuid: string, borderColor: string): Promise<void>;
  editor_canvas_getLocation(uuid: string): Promise<number[]>;
  editor_canvas_getAbsoluteLocation(uuid: string): Promise<number[]>;
  editor_canvas_getDimensions(uuid: string): Promise<number[]>;
  editor_canvas_center(uuid: string): Promise<void>;
  editor_canvas_draw(): Promise<void>;
}

/**
 * Represents an element in a graph, either a Node or an Edge.
 */
export class Element {
  /**
   * The unique element's identifier in the graph.
   */
  uuid: string;
  /**
   * A representation for the domain model object, associated to this element.
   */
  definition: Definition;
}

/**
 * Represents a vertex in a graph.
 */
export class Node extends Element {
  /**
   * The incomming edges (in this node).
   */
  inEdges: Edge[];
  /**
   * The outgoing edges (from this node).
   */
  outEdges: Edge[];
}

/**
 * Represents a connection between vertexes, in a graph.
 */
export class Edge extends Element {
  /**
   * The source node's identifier (UUID), if any.
   */
  source: string;
  /**
   * The target node's identifier (UUID), if any.
   */
  target: string;
}

/**
 * Represents a domain model object by some of its attributes.
 * It does not correspond to the object in memory for the domain model.
 */
export class Definition {
  /**
   * The domain model object's identifier.
   * It is used to match with FQCN of the object's fully qualified class name.
   * Examples:
   * - org.kie.workbench.common.stunner.sw.definition.InjectState
   * - org.kie.workbench.common.stunner.sw.definition.OperationState
   * - org.kie.workbench.common.stunner.sw.definition.End
   */
  id: string;
  /**
   * The domain model object's name.
   * Returns the value for the name field for the object that this definition represents (eg: the name of an InjectState, or an OperationState).
   * The returned name is also the one being displayed on the shape.
   */
  name: string;
}
