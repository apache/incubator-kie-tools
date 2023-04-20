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
   * as well other methods related to the management of the editor's view.
   */
  canvas: SwfStunnerEditorCanvas;
}

/**
 * The diagram editor's session object (API).
 */
export interface SwfStunnerEditoSession {
  /**
   * It returns all nodes' identifiers (UUID).
   * @returns Asyncrhonously returns the UUID for all nodes in graph.
   */
  getAllNodesUUID(): Promise<string[]>;
  /**
   * It return the Edge for the given identifier (UUID).
   * @param uuid The edge's identifier (UUID).
   * @returns Asyncrhonously returns the edge object.
   */
  getEdgeByUUID(uuid: string): Promise<Edge>;
  /**
   * It return the Node for the given identifier (UUID).
   * @param uuid The node's identifier (UUID).
   * @returns Asyncrhonously returns the node object.
   */
  getNodeByUUID(uuid: string): Promise<Node>;
  /**
   * It returns the node's or edge's definition object.
   * The definition object must be cloneable, in order to travel across the envelope bus.
   * So it is up to third parties to ensure the object is cloneable, otherwise this method will return an exception.
   * @param uuid The node's or edge's identifier (UUID).
   * @returns Asyncrhonously returns the definition object (eg: InjectState object)
   */
  getDefinitionByElementUUID(uuid: string): Promise<Object>;
  /**
   * It return the Node which has a given name.
   * @param uuid The node's name.
   * @returns Asyncrhonously returns the node object.
   */
  getNodeByName(name: string): Promise<Node>;
  /**
   * It return the name for a Node.
   * @param uuid The node's identifier (UUID).
   * @returns Asyncrhonously returns the name.
   */
  getNodeName(uuid: string): Promise<string>;
  /**
   * It returns the graph element (Node, Edge) identifier (UUID) for the selected shape in the editor's view.
   * @returns Asyncrhonously returns the element's identifier (UUID).
   */
  getSelectedElementUUID(): Promise<string>;
  /**
   * It returns the Node identifier (UUID) for the selected shape in the editor's view.
   * @returns Asyncrhonously returns the node object.
   */
  getSelectedNode(): Promise<Node>;
  /**
   * It returns the Edge identifier (UUID) for the selected shape in the editor's view.
   * @returns Asyncrhonouly returns the edge object.
   */
  getSelectedEdge(): Promise<Edge>;
  /**
   * It returns the node's or edge's definition object, for the selected shape in the editor's view.
   * @returns Asyncrhonously returns the node's or edge's defnition object (eg: InjectState object).
   */
  getSelectedDefinition(): Promise<Object>;
  /**
   * Selects an element (Node or Edge).
   * @param uuid The elements's identifier (UUID).
   * @returns Asyncrhonously notifies about selection being performed.
   */
  selectByUUID(uuid: string): Promise<void>;
  /**
   * Selects an element (Node or Edge).
   * @param uuid The elements's name.
   * @returns Asyncrhonously notifies about selection being performed.
   */
  selectByName(name: string): Promise<void>;
  /**
   * Clears the selection state, also updates the editor's view.
   * @returns Asyncrhonously notifies about selection being properly cleaned.
   */
  clearSelection(): Promise<void>;
}

/**
 * The diagram editor's canvas object (API).
 */
export interface SwfStunnerEditorCanvas {
  /**
   * Looks for all canvas' shape identifiers.
   * Notice result may not match with all graph elements' identifiers.
   * @returns Asyncrhonously returns the identifiers for canvas' shapes.
   */
  getShapeIds(): Promise<string[]>;
  /**
   * Obtain the shape's background (fill) color.
   * @param uuid The shape's identifier. It uses to match with the graph element's identifier.
   * @returns Asyncrhonously returns the background color (name or RGB formatted value) for the shape.
   */
  getBackgroundColor(uuid: string): Promise<string>;
  /**
   * Sets the shape's background (fill) color.
   * @param uuid The shape's identifier. It uses to match with the graph element's identifier.
   * @param backgroundColor The color name or RGB value for the background color.
   * @returns Asyncrhonously notifies when the background color is being changed. Notice you may call draw once all attributes have been changed.
   */
  setBackgroundColor(uuid: string, backgroundColor: string): Promise<void>;
  /**
   * Obtain the shape's border (stroke) color.
   * @param uuid The shape's identifier. It uses to match with the graph element's identifier.
   * @returns Asyncrhonously returns the border color (name or RGB formatted value) for the shape.
   */
  getBorderColor(uuid: string): Promise<string>;
  /**
   * Sets the shape's border (stroke) color.
   * @param uuid The shape's identifier. It uses to match with the graph element's identifier.
   * @param backgroundColor The color name or RGB value for the border color.
   * @returns Asyncrhonously notifies when the border color is being changed. Notice you may call draw once all attributes have been changed.
   */
  setBorderColor(uuid: string, backgroundColor: string): Promise<void>;
  /**
   * Obtain the shape's location relative to the parent, if any.
   * @param uuid The shape's identifier. It uses to match with the graph element's identifier.
   * @returns Asyncrhonously returns the location (X and Y coordinates in a 2D space) for the given shape.
   */
  getLocation(uuid: string): Promise<number[]>;
  /**
   * Obtain the shape's location relative to the viewport.
   * @param uuid The shape's identifier. It uses to match with the graph element's identifier.
   * @returns Asyncrhonously returns the absolute location (X and Y coordinates in a 2D space) for the given shape.
   */
  getAbsoluteLocation(uuid: string): Promise<number[]>;
  /**
   * Compute the shape's dimensions.
   * @param uuid The shape's identifier. It uses to match with the graph element's identifier.
   * @returns Asyncrhonously returns the width and height values (in PX units) for the given shape.
   */
  getDimensions(uuid: string): Promise<number[]>;
  /**
   * Changes the viewport accordingly to render a shape in its center.
   * Only applies when shapes is not visible at all, in the viewport area.
   * @param uuid The shape's identifier. It uses to match with the graph element's identifier.
   * @returns Asyncrhonously notifies when operations are being completed.
   */
  center(uuid: string): Promise<void>;
  /**
   * It performs the screen rendering for the changed objects or shapes' attributes in the editor's view.
   * @returns Asyncrhonously notifies when operations are being completed.
   */
  draw(): Promise<void>;
}
