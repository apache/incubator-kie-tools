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

import { Node, Edge } from "./StunnerEditorEnvelopeAPI";

/**
 * Javascript's main entry point for the 'Serverless Workflow Diagram Editor' API.
 * This API is exposed in the editor's envelope, so it allows communication between app-shells by relying on the envelope bus.
 */
export interface SwfStunnerEditorAPI {
  /**
   * The diagram editor's session object. It provides access to the
   * editor's model objects, as well as access to controls like selection.
   */
  session: SwfStunnerEditoSession;
  /**
   * The diagram editor's canvas object. It allows to get or update shape attributes' values,
   * as well as other methods related to the management of the editor's view.
   */
  canvas: SwfStunnerEditorCanvas;
}

/**
 * The diagram editor's session object (API).
 */
export interface SwfStunnerEditoSession {
  /**
   * Returns all nodes' identifiers (UUID).
   * @returns Asynchronously returns the UUID for all nodes in graph.
   */
  getAllNodesUUID(): Promise<string[]>;
  /**
   * Returns the Edge for the given identifier (UUID).
   * @param uuid The edge's identifier (UUID).
   * @returns Asynchronously returns the edge object.
   */
  getEdgeByUUID(uuid: string): Promise<Edge>;
  /**
   * Returns the Node for the given identifier (UUID).
   * @param uuid The node's identifier (UUID).
   * @returns Asynchronously returns the node object.
   */
  getNodeByUUID(uuid: string): Promise<Node>;
  /**
   * Returns the node's or edge's definition object.
   * The definition object must be cloneable, in order to travel across the envelope bus.
   * It is up to third parties to ensure the object is cloneable, otherwise this method will return an exception.
   * @param uuid The node's or edge's identifier (UUID).
   * @returns Asynchronously returns the definition object (eg: InjectState object)
   */
  getDefinitionByElementUUID(uuid: string): Promise<Object>;
  /**
   * Returns the Node which has a given name.
   * @param uuid The node's name.
   * @returns Asynchronously returns the node object.
   */
  getNodeByName(name: string): Promise<Node>;
  /**
   * Returns the name for a Node.
   * @param node The Node instance.
   * @returns Asynchronously returns the node's name.
   */
  getNodeName(node: Node): Promise<string>;
  /**
   * Returns the graph element (Node, Edge) identifier (UUID) for the selected shape in the editor's view.
   * @returns Asynchronously returns the element's identifier (UUID).
   */
  getSelectedElementUUID(): Promise<string>;
  /**
   * Returns the Node identifier (UUID) for the selected shape in the editor's view.
   * @returns Asynchronously returns the node object.
   */
  getSelectedNode(): Promise<Node>;
  /**
   * Returns the Edge identifier (UUID) for the selected shape in the editor's view.
   * @returns Asynchronously returns the edge object.
   */
  getSelectedEdge(): Promise<Edge>;
  /**
   * Returns the node's or edge's definition object, for the selected shape in the editor's view.
   * @returns Asynchronously returns the node's or edge's definition object (eg: the InjectState object).
   */
  getSelectedDefinition(): Promise<Object>;
  /**
   * Selects an element (Node or Edge).
   * @param uuid The elements's identifier (UUID).
   * @returns Asynchronously notifies about selection being performed.
   */
  selectByUUID(uuid: string): Promise<void>;
  /**
   * Selects an element (Node or Edge).
   * @param uuid The elements's name.
   * @returns Asynchronously notifies about selection being performed.
   */
  selectByName(name: string): Promise<void>;
  /**
   * Clears the selection state and  updates the editor's view.
   * @returns Asynchronously notifies about selection being properly cleaned.
   */
  clearSelection(): Promise<void>;
}

/**
 * The diagram editor's canvas object (API).
 */
export interface SwfStunnerEditorCanvas {
  /**
   * Looks for all canvas' shape identifiers.
   * Returned values may not match with all graph elements' identifiers.
   * @returns Asynchronously returns the identifiers for canvas' shapes.
   */
  getShapeIds(): Promise<string[]>;
  /**
   * Obtains the shape's background (fill) color.
   * @param uuid The shape's identifier. It uses to match with the graph element's identifier.
   * @returns Asynchronously returns the background color (name or RGB formatted value) for the shape.
   */
  getBackgroundColor(uuid: string): Promise<string>;
  /**
   * Sets the shape's background (fill) color.
   * @param uuid The shape's identifier. It uses to match with the graph element's identifier.
   * @param backgroundColor The color name or RGB value for the background color.
   * @returns Asynchronously notifies when the background color is being changed. Notice you may need to call a draw function once all attributes have been changed.
   */
  setBackgroundColor(uuid: string, backgroundColor: string): Promise<void>;
  /**
   * Obtains the shape's border (stroke) color.
   * @param uuid The shape's identifier. It is used to match with the graph element's identifier.
   * @returns Asynchronously returns the border color (name or RGB formatted value) for the shape.
   */
  getBorderColor(uuid: string): Promise<string>;
  /**
   * Sets the shape's border (stroke) color.
   * @param uuid The shape's identifier. It is used to match with the graph element's identifier.
   * @param borderColor The color name or RGB value for the border color.
   * @returns Asynchronously notifies when the border color is being changed. Notice you may need to call a draw function once all attributes have been changed.
   */
  setBorderColor(uuid: string, borderColor: string): Promise<void>;
  /**
   * Obtains the shape's location relative to the parent, if any.
   * @param uuid The shape's identifier. It is used to match with the graph element's identifier.
   * @returns Asynchronously returns the location (X and Y coordinates in a 2D space) for the given shape.
   */
  getLocation(uuid: string): Promise<number[]>;
  /**
   * Obtains the shape's location relative to the viewport.
   * @param uuid The shape's identifier. It is used to match with the graph element's identifier.
   * @returns Asynchronously returns the absolute location (X and Y coordinates in a 2D space) for the given shape.
   */
  getAbsoluteLocation(uuid: string): Promise<number[]>;
  /**
   * Computes the shape's dimensions.
   * @param uuid The shape's identifier. It is used to match with the graph element's identifier.
   * @returns Asynchronously returns the width and height values (in PX units) for the given shape.
   */
  getDimensions(uuid: string): Promise<number[]>;
  /**
   * Changes the viewport accordingly to render a shape in its center.
   * Only applies when shapes are not visible at all, in the viewport area.
   * @param uuid The shape's identifier. It is used to match with the graph element's identifier.
   * @returns Asynchronously notifies when operations are being completed.
   */
  center(uuid: string): Promise<void>;
  /**
   * Performs the screen rendering for the changed objects or shapes' attributes in the editor's view.
   * @returns Asynchronously notifies when operations are being completed.
   */
  draw(): Promise<void>;
}
