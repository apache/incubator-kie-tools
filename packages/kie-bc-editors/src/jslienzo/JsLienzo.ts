/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

/**
 * Javascript API for lienzo that allows interactions with nodes and connectors
 * in Kogito editors.
 */
export interface JsLienzo {
  /**
   * Returns ID attributes of all nodes displayed in editors canvas.
   */
  getNodeIds(): string[];

  /**
   * Returns a background color of a node with provided UUID.
   * Returned string is a hex number of the color.
   *
   * @param UUID ID attribute of the queried node
   */
  getBackgroundColor(UUID: string): string;

  /**
   * Sets a background color of a node with provided UUID.
   *
   * @param UUID ID attribute of the target node
   * @param backgroundColor hex number of the desired color as string
   */
  setBackgroundColor(UUID: string, backgroundColor: string): void;

  /**
   * Returns a border color of a node with provided UUID.
   * Returned string is a hex number of the color.
   *
   * @param UUID ID attribute of the target node
   */
  getBorderColor(UUID: string): string;

  /**
   * Sets a border color of a node with provided UUID.
   *
   * @param UUID ID attribute of the target node
   * @param backgroundColor hex number of the desired color as string
   */
  setBorderColor(UUID: string, backgroundColor: string): void;

  /**
   * Returns a canvas location of a node with provided UUID.
   * Returns an array where first position is X-attribute
   * and second position is Y-attribute in the context of canvas.
   *
   * @param UUID ID attribute of the target node
   */
  getLocation(UUID: string): number[];

  /**
   * Returns a window location fo a node with provided UUID.
   * Returns an array where first position is X-attribute
   * and second position is Y-attribute in the context of window
   *
   * @param UUID ID attribute of target node
   */
  getAbsoluteLocation(UUID: string): number[];

  /**
   * Returns dimensions of a node with provided UUID.
   *
   * @param UUID ID attribute of a target node
   */
  getDimensions(UUID: string): number[];
}

/**
 * Bridge for Envelope to connect Javascript API for lienzo with envelope.
 * See {@see JsLienzo} for full documenation for the API.
 */
export interface JsLienzoEnvelopeApi {
  jsLienzo_getNodeIds(): Promise<string[]>;

  jsLienzo_getBackgroundColor(UUID: string): Promise<string>;

  jsLienzo_setBackgroundColor(UUID: string, backgroundColor: string): Promise<void>;

  jsLienzo_getBorderColor(UUID: string): Promise<string>;

  jsLienzo_setBorderColor(UUID: string, borderColor: string): Promise<void>;

  jsLienzo_getLocation(UUID: string): Promise<number[]>;

  jsLienzo_getAbsoluteLocation(UUID: string): Promise<number[]>;

  jsLienzo_getDimensions(UUID: string): Promise<number[]>;
}
